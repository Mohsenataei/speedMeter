package co.avalinejad.iq.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.avalinejad.iq.fragment.SpeedMeterFragment
import co.avalinejad.iq.R


const val fragName = "FRAGMENT_NAME"
const val fragExtraName = "FRAGMENT_EXTRA_DATA_NAME"
const val fragExtraValueTag = "FRAGMENT_EXTRA_DATA_TAG"

class InnerPageActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inner_page)

        showFragment(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { showFragment(it) }
    }


    private fun showFragment(intent: Intent) {
        val frag = intent.getStringExtra(fragName)!!
        val extraName = intent.getStringExtra(fragExtraName)
        val extraValue = intent.getIntExtra(fragExtraValueTag, -1)

        val newfrag = SpeedMeterFragment.newInstance(extraValue, extraName)

        supportFragmentManager.beginTransaction().add(R.id.innerPage, newfrag).commitNow()

        //val fragment = when (frag) {
//            "ExamIntroFragment" -> ExamIntroFragment.newInstance(extraValue)
//            "QuestionContainerFragment" -> QuestionContainerFragment.newInstance(extraValue)
//            "ExamResultFragment" -> ExamResultFragment.newInstance(extraValue)
//            "QuestionPageFragment" -> QuestionPageFragment.newInstance(extraName)
//            "ResultFragment" -> ResultFragment.newInstance(extraValue)
//            "QuestionIntroFragment" -> QuestionIntroFragment.newInstance(extraName)
//            "ExamListFragment" -> ExamListFragment.newInstance()
           // "SpeedMeterFragment" -> SpeedMeterFragment.newInstance(extraValue, extraName)
//            else -> Toast.makeText(this,"عه چرا اومد اینجا ؟",Toast.LENGTH_LONG).show()
//            // ExamListFragment.newInstance()
//        }
//        supportFragmentManager.beginTransaction().add(R.id.mycontainer, fragment).commitNow()
    }
}