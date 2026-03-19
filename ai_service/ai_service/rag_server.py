from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import chromadb
from sentence_transformers import SentenceTransformer

app = FastAPI()

# 1. 初始化数据库与模型 (确保路径与之前 ingest.py 一致)
client = chromadb.PersistentClient(path="./my_vectordb")
collection = client.get_collection(name="interview_repo")
model = SentenceTransformer('shibing624/text2vec-base-chinese')

class SearchRequest(BaseModel):
    query: str
    top_k: int = 3

@app.post("/search")
async def search_alumni_data(request: SearchRequest):
    try:
        # 将 Java 传来的 query 向量化
        query_vector = model.encode(request.query).tolist()

        # 向量检索
        results = collection.query(
            query_embeddings=[query_vector],
            n_results=request.top_k
        )

        # 返回面经原文列表 (documents)
        return {"documents": results['documents'][0]}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    # 启动在 5000 端口
    uvicorn.run(app, host="0.0.0.0", port=5000)