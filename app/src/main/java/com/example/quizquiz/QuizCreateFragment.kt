package com.example.quizquiz
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quizquiz.database.Quiz
import com.example.quizquiz.database.QuizDatabase

class QuizCreateFragment : Fragment() {
    lateinit var db : QuizDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.quiz_create_fragment, container, false)

        // requireContext() <- context 제공해주는 코드
        db = QuizDatabase.getInstance(requireContext())

        // 버튼 setOnclick
        view.findViewById<Button>(R.id.quiz_add).setOnClickListener {
            // 버튼 누르면 EditText 값을 추출해서 Quiz 객체 만들기
            val question = view.findViewById<EditText>(R.id.quiz_question).text.toString()
            val answer = view.findViewById<EditText>(R.id.quiz_answer).text.toString()
            val category = view.findViewById<EditText>(R.id.quiz_category).text.toString()

            val q = Quiz(
                type="ox",
                question = question,
                answer = answer,
                category = category)

            // 만든 Quiz 객체를 DB에 insert 메서드로 삽입
            Thread(Runnable {
                db.quizDAO().insert(q)
            }).start()
        }
        return view
    }
}