package com.gaumala.openjisho.backend.setup

/**
 * Checkpoints of steps during setup.
 * [[SetupCheckpointManager]] can store flags for each checkpoint
 * so that when the user restarts a failed the setup the completed
 * steps are skipped.
 */
enum class Checkpoint {
    /* The RADKFILE table has been successfully populated */
    radkfileReady,
    /* The JMdict table has been successfully populated */
    jmdictReady,
    /* The KANJIDIC table has been successfully populated */
    kanjidicReady,
    /* The Tatoeba sentences file has been downloaded */
    sentencesDownloaded,
    /* The Tatoeba sentences file has been downloaded and decompressed */
    sentencesRetrieved,
    /* The Tatoeba translations file has been downloaded */
    translationsDownloaded,
    /* The Tatoeba translations file has been downloaded and decompressed */
    translationsRetrieved,
    /* The Tatoeba indices file has been downloaded */
    indicesDownloaded,
    /* The Tatoeba indices file has been downloaded and decompressed */
    indicesRetrieved,
    /* The Tatoeba japanese sentences table has been successfully populated */
    sentencesReady,
    /* The Tatoeba japaneseindices table has been successfully populated */
    indicesReady,
    /* The Tatoeba english translations table has been successfully populated */
    translationsReady,
}