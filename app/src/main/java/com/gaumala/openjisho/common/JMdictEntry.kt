package com.gaumala.openjisho.common

import android.content.Context
import android.os.Parcelable
import com.gaumala.openjisho.R
import kotlinx.android.parcel.Parcelize
import java.lang.NumberFormatException


@Parcelize
data class JMdictEntry(val entryId: Long,
                       val kanjiElements: List<Element>,
                       val readingElements: List<Element>,
                       val senseElements: List<Sense>): Parcelable {

    @Parcelize
    data class Element(val text: String, val tags: List<Tag>): Parcelable

    @Parcelize
    data class Sense(val glossItems: List<String>,
                     val glossTags: List<Tag>): Parcelable


    abstract class Tag: Parcelable {

        abstract fun getText(ctx: Context): String
        abstract fun getRawTag(): String

        @Parcelize
        data class Simple(val entity: Entity, val raw: String): Tag() {

            override fun getText(ctx: Context) =
                ctx.getString(entity.stringResId)

            override fun getRawTag() = raw
        }

        @Parcelize
        data class Parametrized(val entity: Entity, val param: Int, val raw: String): Tag() {
            override fun getText(ctx: Context): String =
                String.format(ctx.getString(entity.stringResId), param)


            override fun getRawTag(): String = raw
        }

        @Parcelize
        data class Unknown(val rawText: String): Tag() {
            override fun getText(ctx: Context): String = rawText

            override fun getRawTag(): String = rawText
        }


        companion object {

            private fun parseFrequencyNumber(text: String): Int {
                return try {
                    text.substring(2).toInt() * 500
                } catch (ex: NumberFormatException) {
                    Int.MAX_VALUE
                }
            }

            fun parse(text: String): Tag {
                return when (val entity = Entity.parse(text)) {
                    Entity.numberFreq ->
                        Parametrized(entity, parseFrequencyNumber(text), text)
                    null -> Unknown(text)
                    else -> Simple(entity, text)
                }
            }
        }
    }

    enum class Entity {
        martialArts,
        xRated,
        abbreviation,
        adjectiveI,
        adjectiveIx,
        adjectiveNa,
        adjectiveNo,
        adjectivePreNoun,
        adjectiveTaru,
        adjectiveF,
        adverb,
        adverbTo,
        archaism,
        auxiliary,
        ateji,
        auxVerb,
        auxAdjective,
        buddhist,
        chemistry,
        children,
        colloquialism,
        computer,
        conjuction,
        copula,
        counter,
        derogatory,
        exclusiveKanji,
        exclusiveKana,
        expressions,
        familar,
        female,
        food,
        geometry,
        gikun,
        honorific,
        humble,
        irregKanji,
        idiomatic,
        irregKana,
        interjection,
        irregOkurigana,
        irregVerb,
        linguistics,
        mangaSlang,
        male,
        maleSlang,
        math,
        military,
        noun,
        nounAdv,
        nounSuffix,
        nounPrefix,
        nounTemp,
        numeric,
        outdatedKanji,
        obsolete,
        obscure,
        obsoleteKana,
        oldIrregKana,
        onomatopeia,
        pronoun,
        poetical,
        polite,
        prefix,
        proverb,
        particle,
        physics,
        quote,
        rare,
        sensitive,
        slang,
        suffix,
        usuallyKanji,
        usuallyKana,
        unclassified,
        yojijukugo,
        verb1,
        verb1Kureru,
        verb2U,
        verb4Fu,
        verb4Ru,
        verb5Aru,
        verb5Bu,
        verb5Gu,
        verb5Ku,
        verb5Iku,
        verb5Mu,
        verb5Nu,
        verb5Ru,
        verb5IrregRu,
        verb5Su,
        verb5Tsu,
        verb5U,
        verb5USpecial,
        verb5Uru,
        verb1Zuru,
        verbIntransitive,
        verbKuru,
        verbIrregNu,
        verbIrregRu,
        verbSuru,
        verbSu,
        verbSuruSpecial,
        verbSuruIncluded,
        kyotoBen,
        osakaBen,
        kansaiBen,
        kantouBen,
        tosaBen,
        touhokuBen,
        tsugaruBen,
        kyuushuuBen,
        ryuuKyuuBen,
        naganoBen,
        hokkaidoBen,
        verbTransitive,
        vulgar,
        adjectiveKari,
        adjectiveKu,
        adjectiveShiku,
        adjectiveNari,
        properNoun,
        verbUnspecified,
        verb4Ku,
        verb4Gu,
        verb4Su,
        verb4Tsu,
        verb4Nu,
        verb4Bu,
        verb4Mu,
        verb2UpperKu,
        verb2UpperGu,
        verb2UpperTsu,
        verb2UpperDzu,
        verb2UpperFu,
        verb2UpperBu,
        verb2UpperMu,
        verb2UpperYu,
        verb2UpperRu,
        verb2LowerKu,
        verb2LowerGu,
        verb2LowerSu,
        verb2LowerZu,
        verb2LowerTsu,
        verb2LowerDzu,
        verb2LowerNu,
        verb2LowerFu,
        verb2LowerBu,
        verb2LowerMu,
        verb2LowerYu,
        verb2LowerRu,
        verb2LowerU,
        architecture,
        astronomy,
        baseball,
        biology,
        botany,
        business,
        economics,
        engineering,
        finance,
        geology,
        law,
        mahjong,
        medicine,
        music,
        shinto,
        shogi,
        sports,
        sumo,
        zoology,
        humorous,
        anatomy,
        numberFreq;

        val stringResId: Int
            get() = when (this) {
                martialArts -> R.string.martial_arts_tag
                xRated -> R.string.x_rated_tag
                abbreviation -> R.string.abbreviation_tag
                adjectiveI -> R.string.adjective_i_tag
                adjectiveIx -> R.string.adjective_ix_tag
                adjectiveNa -> R.string.adjective_na_tag
                adjectivePreNoun -> R.string.adjective_pre_noun_tag
                adjectiveTaru -> R.string.taru_adjective_tag
                adjectiveNo -> R.string.adjective_no_tag
                adjectiveF -> R.string.adjective_f_tag
                adverb -> R.string.adverb_tag
                adverbTo -> R.string.adverb_to_tag
                archaism -> R.string.archaism_tag
                auxiliary -> R.string.auxiliary_tag
                ateji -> R.string.ateji_tag
                auxVerb -> R.string.aux_verb_tag
                auxAdjective -> R.string.aux_adjective_tag
                buddhist -> R.string.buddhist_tag
                chemistry -> R.string.chemistry_tag
                children -> R.string.children_tag
                colloquialism -> R.string.colloquialism_tag
                computer -> R.string.computer_tag
                conjuction -> R.string.conjuction_tag
                copula -> R.string.copula_tag
                counter -> R.string.counter_tag
                derogatory -> R.string.derogatory_tag
                exclusiveKanji -> R.string.exclusive_kanji_tag
                exclusiveKana -> R.string.exclusive_kana_tag
                expressions -> R.string.expressions_tag
                familar -> R.string.familiar_tag
                female -> R.string.female_tag
                food -> R.string.food_tag
                geometry -> R.string.geometry_tag
                gikun -> R.string.gikun_tag
                honorific -> R.string.honorific_tag
                humble -> R.string.humble_tag
                irregKanji -> R.string.irreg_kanji_tag
                idiomatic -> R.string.idiomatic_tag
                irregKana -> R.string.irreg_kana_tag
                interjection -> R.string.interjection_tag
                irregOkurigana -> R.string.irreg_okurigana_tag
                irregVerb -> R.string.irreg_verb_tag
                linguistics -> R.string.longuistics_tag
                mangaSlang -> R.string.male_slang_tag
                male -> R.string.male_tag
                maleSlang -> R.string.male_slang_tag
                math -> R.string.math_tag
                military -> R.string.military_tag
                noun -> R.string.noun_tag
                nounAdv -> R.string.noun_adverb_tag
                nounSuffix -> R.string.noun_suffix_tag
                nounPrefix -> R.string.noun_prefix_tag
                nounTemp -> R.string.noun_temp_tag
                numeric -> R.string.numeric_tag
                outdatedKanji -> R.string.outdated_kanji_tag
                obsolete -> R.string.obsolete_tag
                obscure -> R.string.obscure_tag
                obsoleteKana -> R.string.obsolete_kana_tag
                oldIrregKana -> R.string.old_irreg_kana_tag
                onomatopeia -> R.string.onomatopeia_tag
                pronoun -> R.string.pronoun_tag
                poetical -> R.string.poetical_tag
                polite -> R.string.polite_tag
                prefix -> R.string.prefix_tag
                proverb -> R.string.proverb_tag
                particle -> R.string.particle_tag
                physics -> R.string.physics_tag
                quote -> R.string.quote_tag
                rare -> R.string.rare_tag
                sensitive -> R.string.sensitive_tag
                slang -> R.string.slang_tag
                suffix -> R.string.suffix_tag
                usuallyKanji -> R.string.usually_kanji_tag
                usuallyKana -> R.string.usually_kana_tag
                unclassified -> R.string.unclassified_tag
                yojijukugo -> R.string.yojikugo_tag
                verb1 -> R.string.verb1_tag
                verb1Kureru -> R.string.verb1_kureru_tag
                verb2U -> R.string.verb2_u_tag
                verb4Fu -> R.string.verb4_fu_tag
                verb4Ru -> R.string.verb4_ru_tag
                verb5Aru -> R.string.verb5_aru_tag
                verb5Bu -> R.string.verb5_bu_tag
                verb5Gu -> R.string.verb5_gu_tag
                verb5Ku -> R.string.verb5_ku_tag
                verb5Iku -> R.string.verb5_Iku_tag
                verb5Mu -> R.string.verb5_mu_tag
                verb5Nu -> R.string.verb5_nu_tag
                verb5Ru -> R.string.verb5_ru_tag
                verb5IrregRu -> R.string.verb5_irreg_ru_tag
                verb5Su -> R.string.verb5_su_tag
                verb5Tsu -> R.string.verb5_tsu_tag
                verb5U -> R.string.verb5_u_tag
                verb5USpecial -> R.string.verb5_u_special_tag
                verb5Uru -> R.string.verb5_uru_tag
                verb1Zuru -> R.string.verb1_zuru_tag
                verbIntransitive -> R.string.verb_intransitive_tag
                verbIrregNu -> R.string.verb_irreg_nu_tag
                verbIrregRu -> R.string.verb_irreg_ru_tag
                verbKuru -> R.string.verb_kuru_tag
                verbSuru -> R.string.verb_suru_tag
                verbSu -> R.string.verb_su_tag
                verbSuruSpecial -> R.string.verb_suru_special_tag
                verbSuruIncluded -> R.string.verb_suru_included_tag
                kyotoBen -> R.string.kyoto_ben_tag
                osakaBen -> R.string.osaka_ben_tag
                kansaiBen -> R.string.kansai_ben_tag
                kantouBen -> R.string.kantou_ben_tag
                tosaBen -> R.string.tosa_ben_tag
                touhokuBen -> R.string.touhoku_ben_tag
                tsugaruBen -> R.string.tsugaru_ben_tag
                kyuushuuBen -> R.string.kyuushuu_ben_tag
                ryuuKyuuBen -> R.string.ryuukyuu_ben_tag
                naganoBen -> R.string.nagano_ben_tag
                hokkaidoBen -> R.string.hokkaido_ben_tag
                verbTransitive -> R.string.verb_transitive_tag
                vulgar -> R.string.vulgar_tag
                adjectiveKari -> R.string.adjective_kari_tag
                adjectiveKu -> R.string.adjective_ku_tag
                adjectiveShiku -> R.string.adjective_shiku_tag
                adjectiveNari -> R.string.adjective_nari_tag
                properNoun -> R.string.proper_noun_tag
                verbUnspecified -> R.string.verb_unspecified_tag
                verb4Ku -> R.string.verb4_ku_tag
                verb4Gu -> R.string.verb4_gu_tag
                verb4Su -> R.string.verb4_su_tag
                verb4Tsu -> R.string.verb4_tsu_tag
                verb4Nu -> R.string.verb4_nu_tag
                verb4Bu -> R.string.verb4_bu_tag
                verb4Mu -> R.string.verb4_mu_tag
                verb2UpperKu -> R.string.verb2_upper_ku_tag
                verb2UpperGu -> R.string.verb2_upper_gu_tag
                verb2UpperTsu -> R.string.verb2_upper_tsu_tag
                verb2UpperDzu -> R.string.verb2_upper_dzu_tag
                verb2UpperFu -> R.string.verb2_upper_fu_tag
                verb2UpperBu -> R.string.verb2_upper_bu_tag
                verb2UpperMu -> R.string.verb2_upper_mu_tag
                verb2UpperYu -> R.string.verb2_upper_yu_tag
                verb2UpperRu -> R.string.verb2_upper_ru_tag
                verb2LowerKu -> R.string.verb2_lower_ku_tag
                verb2LowerGu -> R.string.verb2_lower_gu_tag
                verb2LowerSu -> R.string.verb2_lower_su_tag
                verb2LowerZu -> R.string.verb2_lower_zu_tag
                verb2LowerTsu -> R.string.verb2_lower_tsu_tag
                verb2LowerDzu -> R.string.verb2_lower_dzu_tag
                verb2LowerNu -> R.string.verb2_lower_nu_tag
                verb2LowerFu -> R.string.verb2_lower_fu_tag
                verb2LowerBu -> R.string.verb2_lower_bu_tag
                verb2LowerMu -> R.string.verb2_lower_mu_tag
                verb2LowerYu -> R.string.verb2_lower_yu_tag
                verb2LowerRu -> R.string.verb2_lower_ru_tag
                verb2LowerU -> R.string.verb2_lower_u_tag
                architecture -> R.string.architecture_tag
                astronomy -> R.string.astronomy_tag
                baseball -> R.string.baseball_tag
                biology -> R.string.biology_tag
                botany -> R.string.botany_tag
                business -> R.string.business_tag
                economics -> R.string.economics_tag
                engineering -> R.string.engineering_tag
                finance -> R.string.finance_tag
                geology -> R.string.geology_tag
                law -> R.string.law_tag
                mahjong -> R.string.mahjong_tag
                medicine -> R.string.medicine_tag
                music -> R.string.music_tag
                shinto -> R.string.shinto_tag
                shogi -> R.string.shogi_tag
                sports -> R.string.sports_tag
                sumo -> R.string.sumo_tag
                zoology -> R.string.zoology_tag
                humorous -> R.string.humorous_tag
                anatomy -> R.string.anatomy_tag
                numberFreq -> R.string.top_n_common_tag
            }

        companion object {
            fun parse(text: String): Entity? = when (text) {
                "MA" -> martialArts
                "X" -> xRated
                "abbr" -> abbreviation
                "adj-i" -> adjectiveI
                "adj-ix" -> adjectiveIx
                "adj-na" -> adjectiveNa
                "adj-no" -> adjectiveNo
                "adj-pn" -> adjectivePreNoun
                "adj-t" -> adjectiveTaru
                "adj-f" -> adjectiveF
                "adv" -> adverb
                "adv-to" -> adverbTo
                "arch" -> archaism
                "ateji" -> ateji
                "aux" -> auxiliary
                "aux-v" -> auxVerb
                "aux-adj" -> auxAdjective
                "Buddh" -> buddhist
                "chem" -> chemistry
                "chn" -> children
                "col" -> colloquialism
                "conj" -> conjuction
                "cop-da" -> copula
                "comp" -> computer
                "ctr" -> counter
                "derog" -> derogatory
                "eK" -> exclusiveKanji
                "ek" -> exclusiveKana
                "exp" -> expressions
                "fam" -> familar
                "fem" -> female
                "food" -> food
                "geom" -> geometry
                "gikun" -> gikun
                "hon" -> honorific
                "hum" -> humble
                "iK" -> irregKanji
                "id" -> idiomatic
                "ik" -> irregKana
                "int" -> interjection
                "io" -> irregOkurigana
                "iv" -> irregVerb
                "ling" -> linguistics
                "m-sl" -> mangaSlang
                "male" -> male
                "male-sl" -> maleSlang
                "math" -> math
                "mil" -> military
                "n" -> noun
                "n-adv" -> nounAdv
                "n-suf" -> nounSuffix
                "n-pref" -> nounPrefix
                "n-t" -> nounTemp
                "num" -> numeric
                "oK" -> outdatedKanji
                "obs" -> obsolete
                "obsc" -> obscure
                "ok" -> obsoleteKana
                "oik" -> oldIrregKana
                "on-mim" -> onomatopeia
                "pn" -> pronoun
                "poet" -> poetical
                "pol" -> polite
                "pre" -> prefix
                "proverb" -> proverb
                "prt" -> particle
                "physics" -> physics
                "quote" -> quote
                "rare" -> rare
                "sense" -> sensitive
                "sl" -> slang
                "suf" -> suffix
                "uK" -> usuallyKanji
                "uk" -> usuallyKana
                "unc" -> unclassified
                "yoji" -> yojijukugo
                "v1" -> verb1
                "v1-s" -> verb1Kureru
                "v2a-s" -> verb2U
                "v4h" -> verb4Fu
                "v4r" -> verb4Ru
                "v5aru" -> verb5Aru
                "v5b" -> verb5Bu
                "v5g" -> verb5Gu
                "v5k" -> verb5Ku
                "v5k-s" -> verb5Iku
                "v5m" -> verb5Mu
                "v5n" -> verb5Nu
                "v5r" -> verb5Ru
                "v5r-i" -> verb5IrregRu
                "v5s" -> verb5Su
                "v5t" -> verb5Tsu
                "v5u" -> verb5U
                "v5u-s" -> verb5USpecial
                "v5uru" -> verb5Uru
                "vz" -> verb1Zuru
                "vi" -> verbIntransitive
                "vk" -> verbKuru
                "vn" -> verbIrregNu
                "vr" -> verbIrregRu
                "vs" -> verbSuru
                "vs-c" -> verbSu
                "vs-s" -> verbSuruSpecial
                "vs-i" -> verbSuruIncluded
                "kyb" -> kyotoBen
                "osb" -> osakaBen
                "ksb" -> kansaiBen
                "ktb" -> kantouBen
                "tsb" -> tosaBen
                "thb" -> touhokuBen
                "tsug" -> tsugaruBen
                "kyu" -> kyuushuuBen
                "rkb" -> ryuuKyuuBen
                "nab" -> naganoBen
                "hob" -> hokkaidoBen
                "vt" -> verbTransitive
                "vulg" -> vulgar
                "adj-kari" -> adjectiveKari
                "adj-ku" -> adjectiveKu
                "adj-shiku" -> adjectiveShiku
                "adj-nari" -> adjectiveNari
                "n-pr" -> properNoun
                "v-unspec" -> verbUnspecified
                "v4k" -> verb4Ku
                "v4g" -> verb4Gu
                "v4s" -> verb4Su
                "v4t" -> verb4Tsu
                "v4n" -> verb4Nu
                "v4b" -> verb4Bu
                "v4m" -> verb4Mu

                "v2k-k" -> verb2UpperKu
                "v2g-k" -> verb2UpperGu
                "v2t-k" -> verb2UpperTsu
                "v2d-k" -> verb2UpperDzu
                "v2h-k" -> verb2UpperFu
                "v2b-k" -> verb2UpperBu
                "v2m-k" -> verb2UpperMu
                "v2y-k" -> verb2UpperYu
                "v2r-k" -> verb2UpperRu

                "v2k-s" -> verb2LowerKu
                "v2g-s" -> verb2LowerGu
                "v2s-s" -> verb2LowerSu
                "v2z-s" -> verb2LowerZu
                "v2t-s" -> verb2LowerTsu
                "v2d-s" -> verb2LowerDzu
                "v2n-s" -> verb2LowerNu
                "v2h-s" -> verb2LowerFu
                "v2b-s" -> verb2LowerBu
                "v2m-s" -> verb2LowerMu
                "v2y-s" -> verb2LowerYu
                "v2r-s" -> verb2LowerRu
                "v2w-s" -> verb2LowerU

                "archit" -> architecture
                "astron" -> astronomy
                "baseb" -> baseball
                "biol" -> biology
                "bot" -> botany
                "bus" -> business
                "econ" -> economics
                "engr" -> engineering
                "finc" -> finance
                "geol" -> geology
                "law" -> law
                "mahj" -> mahjong
                "med" -> medicine
                "music" -> music
                "Shinto" -> shinto
                "shogi" -> shogi
                "sports" -> sports
                "sumo" -> sumo
                "zool" -> zoology
                "joc" -> humorous
                "anat" -> anatomy
                else -> {
                    if (text.startsWith("nf"))
                        numberFreq
                    else
                        null
                }
            }

        }


    }

    @Parcelize
    data class Summarized(val header: String,
                          val furigana: String?,
                          val sub: String,
                          val entry: JMdictEntry): Parcelable {
        // This is meant to be used by the FuriganaVew widget
        val itemHeader: String
            get() = if (furigana != null) "{$header;$furigana}"
                    else header

        companion object {
            fun fromEntry(
                entry: JMdictEntry,
                targetHeader: String? = null
            ): Summarized {
                val kanjiElements = entry.kanjiElements
                val readingElements = entry.readingElements
                val senseElements = entry.senseElements

                val sub = senseElements
                    .flatMap { it.glossItems }
                    .joinToString("; ")

                if (kanjiElements.isNotEmpty()) {
                    val header = kanjiElements
                        .map { it.text }
                        .find { it == targetHeader }
                        ?: kanjiElements.first().text
                    val furigana = readingElements.firstOrNull()?.text
                    return Summarized(
                        header = header,
                        furigana = furigana,
                        sub = sub,
                        entry = entry
                    )
                }

                // There should always be at least one reading element but idk
                val header = readingElements.firstOrNull()?.text ?: "N/A"
                return Summarized(
                    header = header,
                    furigana = null,
                    sub = sub,
                    entry = entry
                )
            }
        }
    }
}