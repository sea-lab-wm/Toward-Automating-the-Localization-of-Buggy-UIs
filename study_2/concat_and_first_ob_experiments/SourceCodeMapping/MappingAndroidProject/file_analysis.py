import glob
import re
from xml.etree import cElementTree as ET
import javalang as javalang
from bs4 import BeautifulSoup
import os
import subprocess


class FileAnalysis:
    """
    Analyzes Java source files and XML resources for UI-to-code mapping

    Provides methods to parse Java files, extract class/method information, search
    for terms in source code, and match files based on activities, fragments, and
    component IDs for Android bug localization.
    """

    def get_file_content(self, file_name):
        """
        Reads and returns the complete contents of a file

        Opens the specified file in read mode and returns all text content.

        Arguments:
            file_name: Path to the file to read
        Returns:
            String containing the complete file contents
        """
        # Read the file
        file_content = open(file_name, "r")
        file_content = file_content.read()
        return file_content

    def get_all_java_classes_methods(self, parent_directory):
        """
        Extracts all Java classes and methods from a directory tree

        Recursively scans for Java files, parses them using srcML and javalang to
        extract method bodies and class AST nodes. Used for code analysis and search.

        Arguments:
            parent_directory: Root directory to search for Java files
        Returns:
            Tuple of (class_method_dict mapping filenames to method bodies,
                     list of Java class AST nodes)
        """
        class_method_dict = {}
        java_classes = []
        for filename in sorted(glob.glob(f'{parent_directory}/**/*.java', recursive=True)):
            with open(filename) as java_file:
                java_code = java_file.read()
                command = "srcml " + filename
                class_xml = subprocess.check_output(command, shell=True)

                xmltree = ET.fromstring(class_xml)

                Bs_data = BeautifulSoup(class_xml, "xml")

                functions = Bs_data.find_all('function')

                method_body = []

                for function in functions:
                    method_body.append(function.text)

                class_method_dict[filename] = method_body

                try:
                    tree = javalang.parse.parse(java_code)  # A CompilationUnit (root of AST)
                    java_classes.extend(tree.types)

                except:
                    continue

        return class_method_dict, java_classes

    def get_filtered_files(self, parent_directory, set_of_activities):
        """
        Finds Java files matching activity or fragment names

        Searches for Java files whose basenames or full paths match activity/fragment
        names extracted from UI traces. Supports both simple names and package paths.

        Arguments:
            parent_directory: Root directory to search for Java files
            set_of_activities: List of activity/fragment names or paths to match
        Returns:
            List of file paths matching the activity/fragment names
        """
        predicted_files = []

        for filename in sorted(glob.glob(f'{parent_directory}/**/*.java', recursive=True)):
            basename = os.path.basename(filename)
            basename = basename.split('.')[0]
            activity_exist = False
            for item in set_of_activities:
                if "/" in item:
                    if len(item) > 0 and item is not None and item + ".java" in filename:
                        activity_exist = True
                else:
                    if len(item) > 0 and item is not None and item == basename:
                        activity_exist = True

            if activity_exist == True:
                # print(f'Matched {filename}')
                predicted_files.append(filename)

        return predicted_files

    def get_additional_files(self, parent_directory, list_of_filenames):
        """
        Finds additional Java files matching filename list (similar to get_filtered_files)

        Searches for Java files whose basenames or full paths match provided filenames.
        Functionally similar to get_filtered_files.

        Arguments:
            parent_directory: Root directory to search for Java files
            list_of_filenames: List of filenames or paths to match
        Returns:
            List of file paths matching the filenames
        """
        predicted_files = []

        for filename in sorted(glob.glob(f'{parent_directory}/**/*.java', recursive=True)):
            basename = os.path.basename(filename)
            basename = basename.split('.')[0]
            activity_exist = False
            for item in list_of_filenames:
                # if len(item)>0 and item is not None and item==basename:
                #     activity_exist = True
                if "/" in item:
                    if len(item) > 0 and item is not None and item + ".java" in filename:
                        activity_exist = True
                else:
                    if len(item) > 0 and item is not None and item == basename:
                        activity_exist = True

            if activity_exist == True:
                predicted_files.append(filename)

        return predicted_files

    def get_all_java_files(self, parent_directory):
        """
        Retrieves all Java files from a directory tree

        Recursively finds all .java files under the given directory.

        Arguments:
            parent_directory: Root directory to search
        Returns:
            Sorted list of all Java file paths
        """
        all_files = []

        for filename in sorted(glob.glob(f'{parent_directory}/**/*.java', recursive=True)):
            all_files.append(filename)

        return all_files

    def get_string_ids(self, parent_directory, keyword):
        """
        Finds string resource IDs matching a keyword from strings.xml files

        Searches all strings.xml files for string resources whose text matches the
        keyword and returns their resource IDs.

        Arguments:
            parent_directory: Root directory to search for strings.xml files
            keyword: Text to match in string resource values
        Returns:
            List of string resource IDs (names) matching the keyword
        """
        string_ids = []
        for filename in sorted(glob.glob(f'{parent_directory}/**/strings.xml', recursive=True)):
            # print(filename)
            with open(filename) as xml_file:
                xml_data = xml_file.read()
                Bs_data = BeautifulSoup(xml_data, "xml")
                # print(Bs_data)
                strings = Bs_data.find_all('string')
                # print(strings)
                for string in strings:
                    if string.text == keyword:
                        string_ids.append(string['name'])

        return string_ids

    def get_android_ids(self, parent_directory, keyword):
        """
        Finds Android component IDs with text matching a keyword from layout XML files

        Searches all XML layout files (excluding strings.xml) for UI components whose
        android:text attribute contains the keyword, returning their android:id values.

        Arguments:
            parent_directory: Root directory to search for XML files
            keyword: Text to search for in android:text attributes
        Returns:
            List of Android component IDs (without "@+id/" prefix) matching the keyword
        """
        android_ids = []
        for filename in sorted(glob.glob(f'{parent_directory}/**/*.xml', recursive=True)):
            if 'strings.xml' in filename:
                continue
            # print(filename)
            with open(filename) as xml_file:
                xml_data = xml_file.read()
                soup = BeautifulSoup(xml_data, "xml")
                # print(Bs_data)
                sections = soup.find_all(True)
                # print(strings)
                for string in sections:
                    if string.has_attr('android:text'):
                        if keyword in string['android:text']:
                            if string.has_attr('android:id'):
                                android_id = string['android:id'].split('/')[1]
                                android_ids.append(android_id)
                                # print('a')
                                # print(android_id)
                    # if string.text == keyword:
                    #     string_ids.append(string['name'])

        return android_ids

    def get_start_methods(self, graph_dict, called_method_dict, predicted_files):
        """
        Extracts starting methods from predicted files using call graph

        Identifies entry-point methods in predicted files by matching class names
        with the call graph and extracting their called methods.

        Arguments:
            graph_dict: Call graph dictionary mapping classes to call information
            called_method_dict: Dictionary mapping classes to their called methods
            predicted_files: List of predicted file paths
        Returns:
            List of starting methods from the predicted files
        """
        list_of_start_methods = []
        for java_class in graph_dict:
            match_class_flag = False
            for predicted_file in predicted_files:
                if java_class in predicted_file:
                    match_class_flag = True

            if match_class_flag == True:
                callee_methods = called_method_dict[java_class]
                list_of_start_methods.extend(callee_methods)
        return list_of_start_methods

    def get_filenames_based_on_imports(self, predicted_activity_files):
        """
        Extracts class names from import statements in predicted activity files

        Parses Java files using srcML to extract import statements and derives potential
        related filenames from the last two components of each import path.

        Arguments:
            predicted_activity_files: List of Java file paths to analyze
        Returns:
            List of class names extracted from import statements
        """
        filenames = []
        for activity_file in predicted_activity_files:
            command = "srcml " + activity_file
            class_xml = subprocess.check_output(command, shell=True)

            xmltree = ET.fromstring(class_xml)

            soup = BeautifulSoup(class_xml, "xml")

            imports = soup.find_all('import')
            for import_statement in imports:
                import_splits = import_statement.text.split(".")
                if len(import_splits) > 0:
                    found_filename = import_splits[len(import_splits) - 1]
                    found_filename = found_filename.split(';')[0]
                    filenames.append(found_filename)
                    if len(import_splits) > 1:
                        found_filename = import_splits[len(import_splits) - 2]
                        filenames.append(found_filename)

        return filenames

    # checks the source code of the files and in every method in files in a term exists
    def get_class_other_terms(self, parent_directory, search_terms):
        """
        Finds files and methods containing any of the search terms

        Parses all Java files using srcML, searches method bodies for occurrences of
        any search term (case-insensitive), and returns matching files and methods.

        Arguments:
            parent_directory: Root directory to search for Java files
            search_terms: List of terms to search for in method bodies
        Returns:
            Tuple of (list of matching file paths, list of matching method bodies)
        """
        class_method_list = []
        list_of_other_files = []
        for filename in sorted(glob.glob(f'{parent_directory}/**/*.java', recursive=True)):
            command = "srcml " + filename
            class_xml = subprocess.check_output(command, shell=True)

            xmltree = ET.fromstring(class_xml)

            Bs_data = BeautifulSoup(class_xml, "xml")

            functions = Bs_data.find_all('function')

            for function in functions:
                search_terms = [term.lower() for term in search_terms]
                search_term_exist = self.check_if_term_exist(search_terms, function.text.lower())
                if search_term_exist == True:
                    class_method_list.append(function.text)
                    list_of_other_files.append(filename)

        return list_of_other_files, class_method_list

    # retrieve files where any search term exists
    def get_files_if_term_exists(self, parent_directory, search_terms):
        """
        Finds Java files containing any of the search terms

        Searches entire file contents for occurrences of any search term and returns
        matching files. Used to find files referencing specific component IDs or keywords.

        Arguments:
            parent_directory: Root directory to search for Java files
            search_terms: List of terms to search for in file contents
        Returns:
            List of file paths containing at least one search term
        """
        list_of_matched_files = []
        for filename in sorted(glob.glob(f'{parent_directory}/**/*.java', recursive=True)):
            file_content = self.get_file_content(filename)

            # search_terms = [term.lower() for term in search_terms]
            search_term_exist = self.check_if_term_exist(search_terms, file_content)

            if search_term_exist == True:
                list_of_matched_files.append(filename)

        return list_of_matched_files

    def get_method_block_with_file_name(self, class_method_name, class_method_dict):
        """
        Retrieves method body for a given class and method name

        Parses the class.method name, finds the matching file in the class_method_dict,
        and extracts the method body by matching method signature parts.

        Arguments:
            class_method_name: String in format "ClassName.methodName(params)"
            class_method_dict: Dictionary mapping filenames to lists of method bodies
        Returns:
            Tuple of (class name, method body string)
        """
        class_method_name = class_method_name.split(".")
        class_name = class_method_name[0]
        method_name = class_method_name[1]
        method_name_parts = re.split(r"[(),]", method_name)
        method_name_parts = self.remove_empty_from_list(method_name_parts)
        method_block = ""

        for filename in class_method_dict:
            # print("class")
            # print(class_name)
            # print(filename)
            basename = os.path.basename(filename)
            basename = basename.split('.')[0]
            if class_name == basename:
                method_list = class_method_dict[filename]
                for method in method_list:
                    method_full_name = method.split("{")[0]
                    method_full_name_list = method_full_name.split('\n')
                    if len(method_full_name_list) > 0:
                        method_full_name = method_full_name_list[len(method_full_name_list) - 1]

                    check_flag = True
                    for part in method_name_parts:
                        if len(part) == 0:
                            continue
                        if part not in method_full_name:
                            check_flag = False
                    if check_flag == True:
                        method_block = method

                        break

        return class_name, method_block

    def check_if_term_exist(self, search_terms, method_block):
        """
        Checks if any search term exists in the given text

        Searches for the presence of any keyword from search_terms within the
        provided text block (method body or file content).

        Arguments:
            search_terms: List of keywords to search for
            method_block: Text string to search within
        Returns:
            Boolean indicating whether at least one keyword was found
        """
        match_keyword = False
        for keyword in search_terms:
            if keyword in method_block:
                match_keyword = True

        return match_keyword
