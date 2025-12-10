import requests
import json


class SimilarityCalculation:
    """
    Computes semantic similarity between query and corpus using embeddings

    Interfaces with a local embedding server to calculate cosine similarity scores
    between query text and corpus documents for UI-to-code mapping.
    """

    def compute_similarity(self, query, corpus):
        """
        Calculates cosine similarity scores between query and corpus documents

        Sends query and corpus to a local embedding service endpoint and retrieves
        cosine similarity scores. Uses SentenceBERT or similar embedding model.

        Arguments:
            query: String or list of query text(s) to compare
            corpus: List of corpus document strings to compare against
        Returns:
            List of cosine similarity scores between query and each corpus document
        """
        ENDPOINT = "http://127.0.0.1:9000/embed_cosine_multiple/"

        data = {"query": query, "corpus": corpus}
        headers = {'Content-type': 'application/json'}

        r = requests.post(ENDPOINT, data=json.dumps(data), headers=headers)

        if r.status_code != 200:
            print("Error in data")
        else:
            print(f"Status Code: {r.status_code}, Response: {r.json()}")

        return r.json()["cos_scores"]
