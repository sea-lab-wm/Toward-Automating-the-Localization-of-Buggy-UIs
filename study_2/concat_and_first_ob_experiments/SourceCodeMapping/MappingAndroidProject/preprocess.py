import re
from nltk.tokenize import wordpunct_tokenize


class Preprocess:
    """
    Handles preprocessing operations for source code text analysis

    Provides methods to clean, tokenize, and normalize source code text for
    similarity comparison in UI-to-code mapping tasks.
    """

    def preprocess_file_content(self, file_content):
        """
        Cleans source code by normalizing whitespace and removing special characters

        Replaces newlines with spaces and removes non-alphanumeric characters except
        periods to prepare text for tokenization.

        Arguments:
            file_content: Raw source code text string
        Returns:
            String with normalized whitespace and special characters removed
        """
        # Replace escape character with space
        file_content = file_content.replace("\n", " ")

        # Replace special characters with space
        file_content = re.sub("[^A-Za-z0-9\s.]+", " ", file_content)

        return file_content

    def camel_case_split(self, identifier):
        """
        Splits camelCase or PascalCase identifiers into separate words

        Uses regex to identify word boundaries in camelCase/PascalCase strings,
        handling transitions from lowercase to uppercase and uppercase sequences.

        Arguments:
            identifier: String in camelCase or PascalCase format
        Returns:
            List of individual words extracted from the identifier
        """
        matches = re.finditer('.+?(?:(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])|$)', identifier)
        return [m.group(0) for m in matches]

    def tokenize_file_content(self, file_content):
        """
        Tokenizes source code text into lowercase word tokens

        Splits text into words, filters out single characters and numbers, splits
        camelCase identifiers, and converts all tokens to lowercase.

        Arguments:
            file_content: Preprocessed source code text string
        Returns:
            List of lowercase word tokens
        """
        file_tokens = []

        # Tokenize the file content by spliting it into words
        for token in wordpunct_tokenize(file_content):
            # This avoid having tokens like '.'
            if (len(token) > 1) and (not token.isdigit()):
                # split the camelCase words
                for word in self.camel_case_split(token):
                    file_tokens.append(word.lower())

        return file_tokens

    def get_preprocessed_data(self, contents):
        """
        Performs complete preprocessing pipeline on source code text

        Orchestrates preprocessing, tokenization, and normalization to produce
        a space-separated string of lowercase tokens suitable for similarity comparison.

        Arguments:
            contents: Raw source code text
        Returns:
            String of space-separated lowercase tokens
        """
        contents_processed = self.preprocess_file_content(contents)
        contents_tokens = self.tokenize_file_content(contents_processed)
        contents_string = ' '.join(contents_tokens)
        return contents_string
