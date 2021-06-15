package com.gaumala.openjisho.backend.setup.file

object DictFileSamples {
    val jmdict = """
<?xml version="1.0" encoding="UTF-8"?>
<!-- Rev 1.07 
	Revised POS tags for the adjectives
-->
<!DOCTYPE JMdict [
<!ELEMENT JMdict (entry*)>
<!ELEMENT field (#PCDATA)>
	<!-- Information about the field of application of the entry/sense. 
	When absent, general application is implied. Entity coding for 
	specific fields of application. -->
<!ENTITY joc "jocular, humorous term">
<!ENTITY anat "anatomical term">
]>
<JMdict>
<entry>
<ent_seq>1499320</ent_seq>
<k_ele>
<keb>部屋</keb>
<ke_pri>ichi1</ke_pri>
<ke_pri>news1</ke_pri>
<ke_pri>nf02</ke_pri>
</k_ele>
<r_ele>
<reb>へや</reb>
<re_pri>ichi1</re_pri>
<re_pri>news1</re_pri>
<re_pri>nf02</re_pri>
</r_ele>
<info>
<audit>
<upd_date>2011-12-03</upd_date>
<upd_detl>Entry created</upd_detl>
</audit>
<audit>
<upd_date>2011-12-04</upd_date>
<upd_detl>Entry amended</upd_detl>
</audit>
<audit>
<upd_date>2011-12-13</upd_date>
<upd_detl>Entry amended</upd_detl>
</audit>
<audit>
<upd_date>2012-04-12</upd_date>
<upd_detl>Entry amended</upd_detl>
</audit>
<audit>
<upd_date>2012-04-12</upd_date>
<upd_detl>Entry amended</upd_detl>
</audit>
<audit>
<upd_date>2012-04-12</upd_date>
<upd_detl>Entry amended</upd_detl>
</audit>
</info>
<sense>
<pos>&n;</pos>
<gloss>room</gloss>
</sense>
<sense>
<xref>相撲部屋</xref>
<field>&sumo;</field>
<misc>&abbr;</misc>
<gloss>stable</gloss>
</sense>
</entry>
</JMdict>"""

    val kanjidic = """
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE kanjidic2 [
    <!-- Version 1.6 - April 2008
    This is the DTD of the XML-format kanji file combining information from
    the KANJIDIC and KANJD212 files. It is intended to be largely self-
    documenting, with each field being accompanied by an explanatory
    comment.

    -->
<!ELEMENT kanjidic2 (header,character*)>
<!ELEMENT header (file_version,database_version,date_of_creation)>
]>
<kanjidic2>
<!-- Entry for Kanji: 握 -->
<character>
<literal>握</literal>
<codepoint>
<cp_value cp_type="ucs">63e1</cp_value>
<cp_value cp_type="jis208">16-14</cp_value>
</codepoint>
<radical>
<rad_value rad_type="classical">64</rad_value>
</radical>
<misc>
<grade>8</grade>
<stroke_count>12</stroke_count>
<freq>1003</freq>
<jlpt>1</jlpt>
</misc>
<dic_number>
<dic_ref dr_type="nelson_c">1963</dic_ref>
<dic_ref dr_type="nelson_n">2232</dic_ref>
<dic_ref dr_type="halpern_njecd">585</dic_ref>
<dic_ref dr_type="halpern_kkld">427</dic_ref>
<dic_ref dr_type="heisig">1059</dic_ref>
<dic_ref dr_type="gakken">1467</dic_ref>
<dic_ref dr_type="oneill_kk">1566</dic_ref>
<dic_ref dr_type="moro" m_vol="5" m_page="0324">12366</dic_ref>
<dic_ref dr_type="henshall">999</dic_ref>
<dic_ref dr_type="sh_kk">1714</dic_ref>
<dic_ref dr_type="jf_cards">1370</dic_ref>
<dic_ref dr_type="tutt_cards">1538</dic_ref>
<dic_ref dr_type="kanji_in_context">1257</dic_ref>
<dic_ref dr_type="kodansha_compact">911</dic_ref>
<dic_ref dr_type="maniette">1068</dic_ref>
</dic_number>
<query_code>
<q_code qc_type="skip">1-3-9</q_code>
<q_code qc_type="sh_desc">3c9.17</q_code>
<q_code qc_type="four_corner">5701.4</q_code>
<q_code qc_type="deroo">1372</q_code>
</query_code>
<reading_meaning>
<rmgroup>
<reading r_type="pinyin">wo4</reading>
<reading r_type="korean_r">ag</reading>
<reading r_type="korean_h">악</reading>
<reading r_type="ja_on">アク</reading>
<reading r_type="ja_kun">にぎ.る</reading>
<meaning>grip</meaning>
<meaning>hold</meaning>
<meaning>mould sushi</meaning>
<meaning>bribe</meaning>
<meaning m_lang="fr">s'agripper</meaning>
<meaning m_lang="fr">tenir</meaning>
<meaning m_lang="fr">mouler (sushis)</meaning>
<meaning m_lang="fr">soudoyer</meaning>
<meaning m_lang="es">agarrar</meaning>
<meaning m_lang="es">coger</meaning>
<meaning m_lang="es">asir</meaning>
<meaning m_lang="es">empuñar</meaning>
<meaning m_lang="es">apretar con la mano</meaning>
<meaning m_lang="pt">agarrar</meaning>
<meaning m_lang="pt">segurar</meaning>
<meaning m_lang="pt">moldar sushi</meaning>
<meaning m_lang="pt">suborno</meaning>
</rmgroup>
</reading_meaning>
</character>
</kanjidic2>"""

    val radkfile = """
#
#                           R A D K F I L E
 
# This is the data file that drives the multi-radical lookup method in XJDIC,
# WWWJDIC and possibly other dictionary and related software.
#
$ 力 2
甥伽加嘉架茄迦賀駕劾勘勧協脅勤筋勲袈功効劫捌助鋤勝勢男勅努働動別勉募勃務
霧勇湧幼虜力励劣労肋脇仂劬劭劼劵勁勍勗勞勣勦飭勠勳勵勸娚嬲嫐恊慟懃拗抛撈
朸枷椦沒渤珈痂癆窈笳耡舅莇跏踴釛勒黝
"""

    val tatoebaSentences =
        "1001\tjpn\tこんにちは\n1002\tjpn\tこんばんは\n"
    val tatoebaTranslations =
        "2001\teng\tGood morning\n2002\teng\tGood evening\n"
    val tatoebaIndices =
        "1001\t2001\tこんにちは\n1002\t2002\tこんばんは\n"
}