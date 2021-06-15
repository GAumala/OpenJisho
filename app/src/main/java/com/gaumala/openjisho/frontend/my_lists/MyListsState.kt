package com.gaumala.openjisho.frontend.my_lists

data class MyListsState(val snackbarMsg: MyListsMsg? = null,
                        val pendingDeletions: List<DeletedMetadata> = emptyList(),
                        val lists: LoadedLists)