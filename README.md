# CSC2620_P4_Forsyth_Ledesma_McNulty_Varano
## WordCloud Generator

This repository contains the WordCloudPanel Java class, which provides functionality for generating word clouds based on word frequencies. It offers a graphical user interface (GUI) with filter options to customize the word cloud display.

## Features
- Small Sample is initially loaded, Use File Menu "Open New Directory" to open new directory, which will proccess and open new word cloud from data
- You may either place new Guten Berg data inside the resources folder before the program is run, or do ctrl+o to load up a new directory 

- Generates a word cloud based on a list of word frequencies.
- Provides filter options to customize the words displayed in the word cloud:
    - Words ending in 'ing'
    - Words containing 'ough'
    - Words ending in 'ism'
    - Words starting with 'kn'
    - Words containing 'augh'
    - Author names
    - Words Starting With a User Input
    - Exlude list of words (Provided as comma delineated list by User ) 
    - Words Starting With 'pre'
    - Word Limiter: Allows user to limit top (x) number of most frequent items to be passed to word cloud
#
Word Limit:

- Caches filtered and rendered word cloud images for faster rendering.
- Offers an option to change the limit of top word frequencies used in the word cloud.
- Uses the KennyCason's Kumo library for word cloud generation.

## Prerequisites

- Java 8 or later
- [Kumo Library](https://github.com/kennycason/kumo)

## Usage

1. Clone the repository:

    ```bash
    git clone https://github.com/vern3432/CSC2620_P4_Forsyth_Ledesma_McNulty_Varano.git
    ```

2. Navigate to the project directory:

    ```bash
    cd gutenberg/src/main/java/com/gutenberg
    ```


4. Run the Main.java file to launch the application.
5. The WordCloudPanel class provides a GUI with a sidebar for filter options. Use the checkboxes to apply the desired filters to the word cloud.

5. The WordCloudPanel class provides a graphical user interface with a sidebar for filter options. Use the checkboxes to apply the desired filters to the word cloud.
