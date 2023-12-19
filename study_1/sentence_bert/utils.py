import torch.nn.functional as F
import os

import torch
import json
import evaluation_metrics as em
from sentence_transformers import util


class RealOBQuery:
    """
    This is the RealOBQuery class which contains OB-ID, OB-text, and ground-truth.
    """

    def __init__(self, bug_id, ob_id, ob_in_title, bug_type, ob_category, ob_rating, ob_text, ground_truth):
        self.bug_id = bug_id
        self.ob_id = ob_id
        self.ob_in_title = ob_in_title
        self.bug_type = bug_type
        self.ob_category = ob_category
        self.ob_rating = ob_rating
        self.ob_text = ob_text
        self.ground_truth = ground_truth


class Document:
    """
    This is the Document class which contains the document ID and the document text.
    """

    def __init__(self, doc_id, doc_text):
        self.doc_id = doc_id
        self.doc_text = doc_text


def create_component_documents(screen_components_path, s_id):
    document_list = []

    with open(screen_components_path, 'r') as json_file:
        data = json.load(json_file)
        # print(data)
        for screen_id, screen_components in data.items():
            if not isinstance(screen_components, list):
                continue
            if screen_id != s_id:
                continue

            # print(f'Screen-ID: {screen_id}')
            for component in screen_components:
                component_document = component["resource_id"] + " " + component["text_content"] + " " + component[
                    "type"] + " "
                document_list.append(Document(str(component["component_id"]), component_document))
            # print(screen_document)
    """print(s_id)
    for document in document_list:
        print(document.doc_id, document.doc_text)"""

    return document_list


def create_screen_documents(screen_components_path):
    document_list = []

    with open(screen_components_path, 'r') as json_file:
        data = json.load(json_file)
        # print(data)
        for screen_id, screen_components in data.items():
            if not isinstance(screen_components, list):
                continue
            screen_document = ""

            # print(f'Screen-ID: {screen_id}')
            for component in screen_components:
                screen_document += component["resource_id"] + " " + component["text_content"] + " " + component[
                    "type"] + " "

            document_list.append(Document(screen_id, screen_document))
            # print(screen_document)
    """for document in document_list:
        print(document.doc_id, document.doc_text)"""

    return document_list


# Encode text
def encode(texts, model, tokenizer, device):
    # Tokenize sentences
    encoded_input = tokenizer(texts, padding=True, truncation=True, return_tensors='pt').to(device)
    print(encoded_input)
    # Compute token embeddings
    with torch.no_grad():
        model_output = model(**encoded_input)
    # Perform pooling
    embeddings = mean_pooling(model_output, encoded_input['attention_mask'])
    # Normalize embeddings
    embeddings = F.normalize(embeddings, p=2, dim=1)

    return embeddings


# Mean Pooling - Take average of all tokens
def mean_pooling(model_output, attention_mask):
    token_embeddings = model_output[0]  # First element of model_output contains all token embeddings
    input_mask_expanded = attention_mask.unsqueeze(-1).expand(token_embeddings.size()).float()
    return torch.sum(token_embeddings * input_mask_expanded, 1) / torch.clamp(input_mask_expanded.sum(1), min=1e-9)


def get_documents_ranking(documents_list, query_list, model, device):
    """
    This method will receive a list of OB queries and the screens of an application and return the results of an
    application as a list of lists.
    :param documents_list: path of the folder that contains the screens of an app
    :param query_list: list of OB queries of an application
    :param model: model that is being evaluated
    :param preprocess: preprocess of the CLIP model
    :param device: device where the evaluation will be conducted
    :return: return the results of an application as a list of lists
    """
    model = model.to(device)

    # Initialize a document dictionary
    documents_dict = {}
    # Add all the documents in a dictionary where key is the document ID and value is the preprocessed document
    for document in documents_list:
        document_id = document.doc_id
        documents_dict[document_id] = model.encode(document.doc_text, convert_to_tensor=True, device=device)

    all_query_result = []
    all_query_scores = []
    # Iterate over all the OBs in the OB query list
    for query in query_list:
        # Tokenize the OB text of an OB query
        text = model.encode(query.ob_text, convert_to_tensor=True, device=device)
        # Initialize the score dictionary
        scores = {}
        # Iterate over the documents dictionary
        for document_id, preprocessed_document in documents_dict.items():
            # Calculate the cosine similarity between the OB text and the preprocessed document
            score = util.cos_sim(text, preprocessed_document).item()
            scores[document_id] = score

        # Rank the documents based on the scores
        ranked_documents = sorted(scores.keys(), key=lambda f: -scores[f])
        # print(f'OB-ID: {query.ob_text}\tScore: {score}\t Ranked Documents: {ranked_documents}')
        scores = dict(sorted(scores.items(), key=lambda item: item[1], reverse=True))
        # print("After sorting:")
        # print(f'OB-Text: {query.ob_text}\tScore: {scores}\t Ranked Documents: {ranked_documents}')

        query_result = []
        # print(f'Ground-truth: {query.ground_truth}')
        # Create the result list of an OB
        for document in ranked_documents:
            # print(f'Doc-ID: {document}')
            # print(query.ground_truth)
            if document in query.ground_truth:
                # print(f'Ground-truth: {query.ground_truth}')
                query_result.append(1)
            else:
                query_result.append(0)
        # print('------------------------------------')

        # Add the result of each OB to the all query result list
        all_query_result.append(query_result)
        all_query_scores.append(scores)
    # print(f'All Query Result: {all_query_result}')
    # print(f'All Query Ranked Screens: {all_query_ranked_screens}')
    # Return the results of all queries as a list of lists
    return all_query_result, all_query_scores


def calculate_metrics(results_list):
    """
    This method will calculate the evaluation metrics.
    :param results_list: a list of lists which contains the results of all the applications or all the OBs
    :return: return the evaluation metrics
    """
    mrr = em.mean_reciprocal_rank(results_list)
    # print(f'MRR:{mrr}')
    map = em.mean_average_precision(results_list)
    # print(f'MAP:{map}')
    hit_1 = em.mean_hit_rate_at_k(results_list, 1)
    hit_2 = em.mean_hit_rate_at_k(results_list, 2)
    hit_3 = em.mean_hit_rate_at_k(results_list, 3)
    hit_4 = em.mean_hit_rate_at_k(results_list, 4)
    hit_5 = em.mean_hit_rate_at_k(results_list, 5)
    hit_6 = em.mean_hit_rate_at_k(results_list, 6)
    hit_7 = em.mean_hit_rate_at_k(results_list, 7)
    hit_8 = em.mean_hit_rate_at_k(results_list, 8)
    hit_9 = em.mean_hit_rate_at_k(results_list, 9)
    hit_10 = em.mean_hit_rate_at_k(results_list, 10)
    return mrr, map, hit_1, hit_2, hit_3, hit_4, hit_5, hit_6, hit_7, hit_8, hit_9, hit_10
