package com.example.quizquiz

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.room.Entity
import com.example.quizquiz.database.Quiz
import com.example.quizquiz.database.QuizDatabase
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    lateinit var drawerToggle: ActionBarDrawerToggle
    lateinit var db : QuizDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = QuizDatabase.getInstance(this)

        // XML 파일 역직렬화 하기 위해서
//        db.quizDAO().insert(
//            Quiz(type="OX", question="asdf", answer="", category="?")
//        )

        // SharedPreferences -> 키, 값
        val sp : SharedPreferences = getSharedPreferences(
            "pref", Context.MODE_PRIVATE)
        // 키가 없을 경우 뒤에 기본 값이 나오는데 여기서 기본 값은 Boolean이므로 true
        if(sp.getBoolean("initialized", true)) {
            initQuizDataFromXMLFile()
            val editor = sp.edit()
            editor.putBoolean("initialized", false)
            editor.commit()
        }
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.drawer_nav_view)
        
        // Fragment 관리하는 것
        supportFragmentManager
            .beginTransaction()
            //frame 안에 fragment를 넣음
            .add(R.id.frame, QuizListFragment())
            .commit()

        // 메뉴를 선택했을 때 실행이 된다
        // 메서드는 하나 밖에 없고, 인자도 하나 밖에 없음
        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.quiz_solve -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, QuizFragment())
                        .commit()
                }
                R.id.quiz_manage -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame, QuizListFragment())
                }
            }
                // (메뉴를) 수동으로 닫아 줌
                drawerLayout.closeDrawers()

                    // ture로 return
                    // 모든 일이 정상적으로 끝났으면 true로 retrun
                    true
        }

        drawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.drawer_open,
            R.string.drawer_close
        ){}
        // false 할 일 없음
        // drawerLayout과 햄버거 아이콘과 연결 시켜주는 코드
        // isDrawerIndicatorEnabled 속성을 true로 설정해
        // 액션바의 왼쪽 상단에 위치한 햄버거 아이콘을 통해 내비게이션 드로어를 표시하고 숨길 수 있도록 합니다.
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        // etDisplayHomeAsUpEnabled 메서드를 호출해서 햄버거 아이콘을 표시하고
        // 해당 아이콘을 클릭해 내비게이션 드로어를 열고 닫을 수 있도록 설정함
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // 호출을 안 해도 기능상 문제는 없으나 호출 하는 것이 좋음
        // 햄버거 열면 화살표로 변하고 끄면 햄버거 메뉴로 변하는 것
        drawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(drawerToggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun initQuizDataFromXMLFile() {
        AsyncTask.execute {
            // 새 스레드에서 실행(여기서는 UI를 바꾸면 안된다)
            val stream = assets.open("quizzes.xml")

            // 빌더 패턴 - 복잡한 객체를 생성하는 클래스와 표현하는 클래스를 분리하여,
            // 동일한 절차에서도 서로 다른 표현을 생성하는 방법을 제공한다.
           val docBuilder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
            
            // XML을 역직렬화
            val doc = docBuilder.parse(stream)
            
            // document랑 많이 호환된다
            val quizzesFromXMLDoc = doc.getElementsByTagName("quiz")
            // Quiz 안에 있는 것을 저장하기 위해
            val quizList = mutableListOf<Quiz>()
            for(idx in 0 until quizzesFromXMLDoc.length) {
                // org.w3c.dom 패키지의 Element 클래스 import
                val e = quizzesFromXMLDoc.item(idx) as Element

                // XML 태그나 속성을 마음대로 지정가능
                val type = e.getAttribute("type")
                // 기본적으로 여러 개가 있다고 생각함
                val question = e.getElementsByTagName("question").item(0).textContent
                val answer = e.getElementsByTagName("answer").item(0).textContent
                val category = e.getElementsByTagName("category").item(0).textContent

                when(type) {
                    "ox" -> {
                        quizList.add(
                            Quiz(type=type,
                                question = question,
                                answer = answer,
                                category = category)
                        )
                    }
                    "multiple_choice" -> {
                        val choices = e.getElementsByTagName("choices")
                        val choiceList = mutableListOf<String>() // XML은 무조건 문자열로 가져와야함
                        // until -> 길이의 끝까지
                        for(idx in 0 until choices.length) {
                            choiceList.add(choices.item(idx).textContent)
                        }
                        quizList.add(
                            Quiz(type=type,
                                question = question,
                                answer = answer,
                                category = category,
                                guesses = choiceList))
                    }
                }
                for(quiz in quizList) {
                    db.quizDAO().insert(quiz)
                }
            }
        }
    }
}