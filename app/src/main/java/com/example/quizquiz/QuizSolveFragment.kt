package com.example.quizquiz

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.quizquiz.database.Quiz
import java.lang.Exception

class QuizSolveFragment : Fragment() {
    interface QuizSolveListener {
        fun onAnswerSelected(isCorrect: Boolean)
    }

    lateinit var listener: QuizSolveListener
    lateinit var quiz: Quiz

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (parentFragment is QuizSolveListener) {
            listener = parentFragment as QuizSolveListener
        } else {
            throw Exception("QuizSolveListener 미구현")
        }
    }

    // newInstance 클래스 메서드 (퀴즈 객체를 전달받도록 구현)
    companion object {
        fun newInstance(quiz: Quiz): QuizSolveFragment {
            val fragment = QuizSolveFragment()
            val args = Bundle()
// Parcelable = 만든 객체를 이동시킬 수 있음, 여러 데이터가 하나의 꾸러미(Class) 안에 담겨 있다." 라는 의미를 가지게 됩니다.
// 그래서 이 데이터 꾸러미가 A Activity에서 B Activity로 한꺼번에 전달되고 받아 볼 수 있도록 해 주는 것이
// Parcelable의 의미
            args.putParcelable("quiz", quiz)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(
            R.layout.quiz_solve_fragment, container, false)

        quiz = arguments?.getParcelable("quiz")!!
        view.findViewById<TextView>(R.id.question).text = quiz.question
        // LinearLayout이 ViewGroup을 상속 받기 때문에 작성가능
        val choices = view.findViewById<ViewGroup>(R.id.choices)
        
        val answerSelectListener = View.OnClickListener {
            val guess = (it as Button).text.toString()

            // guess와 퀴즈의 실제 답을 비교해서 listener의
            // onAnswerSelected를 적절히 호출
            //guess -> 누른 답
            // quiz.answer -> 실제 답
            if (guess == quiz.answer) listener.onAnswerSelected(true)
            else listener.onAnswerSelected(false)
        }

        when(quiz.type) {
            "ox" -> {
                for(sign in listOf("o", "x")) {
                    var btn = Button(activity)
                    btn.text = sign
                    btn.setOnClickListener(answerSelectListener)
                    choices.addView(btn)
                }
            }
            "multiple_choice" -> {
                for(sign in quiz.guesses!!) {
                    var btn = Button(activity)
                    btn.text = sign
                    btn.setOnClickListener(answerSelectListener)
                    choices.addView(btn)
                }
            }
        }

        return view
    }
}