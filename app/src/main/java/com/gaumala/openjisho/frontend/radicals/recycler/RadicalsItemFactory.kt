package com.gaumala.openjisho.frontend.radicals.recycler

import com.gaumala.openjisho.common.KanjiStrokesTuple
import com.gaumala.openjisho.frontend.dict.recycler.ErrorItem
import com.gaumala.openjisho.frontend.dict.recycler.LoadingItem
import com.gaumala.openjisho.frontend.radicals.KanjiResults
import com.gaumala.openjisho.frontend.radicals.RadicalIndex
import com.xwray.groupie.viewbinding.BindableItem



class RadicalsItemFactory(private val onRadicalSelected: (RadicalIndex) -> Unit,
                          private val onKanjiSelected: (String) -> Unit) {

    fun generateRadicals(input: List<RadicalIndex>): List<BindableItem<*>> {
        var lastStrokeCount = 0
        return input.fold(ArrayList()) { output: ArrayList<BindableItem<*>>, radicalIndex ->
           if (radicalIndex.strokes != lastStrokeCount) {
               output.add(StrokesHeader(radicalIndex.strokes))
               lastStrokeCount = radicalIndex.strokes
           }
            output.add(RadicalItem(radicalIndex, onRadicalSelected))
            output
        }
    }

    fun generateKanji(input: List<KanjiStrokesTuple>): List<BindableItem<*>> {
        var lastStrokeCount = 0
        return input.fold(ArrayList()) { output: ArrayList<BindableItem<*>>, row ->
            if (row.strokes != lastStrokeCount) {
                output.add(StrokesHeader(row.strokes, isLarge = true))
                lastStrokeCount = row.strokes
            }
            output.add(ResultKanjiItem(row.kanji, onKanjiSelected))
            output
        }
    }

    fun generateResults(input: KanjiResults): List<BindableItem<*>> {
        return when (input) {
            is KanjiResults.Ready -> generateKanji(input.results)
            is KanjiResults.Loading -> listOf(LoadingItem())
            is KanjiResults.Error -> listOf(ErrorItem(input.msg))
        }
    }

}