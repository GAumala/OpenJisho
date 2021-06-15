package com.gaumala.openjisho.frontend.study_list

import com.gaumala.openjisho.common.StudyCard

sealed class StudyListSideEffect {
    data class Write(val items: List<StudyCard>): StudyListSideEffect()
    object Read: StudyListSideEffect()
}