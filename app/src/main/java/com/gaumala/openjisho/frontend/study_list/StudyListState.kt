package com.gaumala.openjisho.frontend.study_list

data class StudyListState(val name: String,
                          val snackbarMsg: StudyListMsg? = null,
                          val backedUpCards: LoadedStudyCards.Ready? = null,
                          val cards: LoadedStudyCards) {
}