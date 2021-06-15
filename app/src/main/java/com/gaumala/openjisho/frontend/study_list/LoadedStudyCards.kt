package com.gaumala.openjisho.frontend.study_list

import com.gaumala.openjisho.common.StudyCard
import com.gaumala.openjisho.common.UIText

sealed class LoadedStudyCards {
    object Loading: LoadedStudyCards()
    data class Ready(val list: List<StudyCard>): LoadedStudyCards()
    data class Error(val message: UIText): LoadedStudyCards()
}
