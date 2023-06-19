from pymongo import MongoClient
import pandas as pd
from tqdm import tqdm
from faker import Faker

fake = Faker()


def create_dataframe():
    data = pd.DataFrame()
    for i in tqdm(range(1), desc='Creating DataFrame'):
        data.loc[i, 'firstName'] = fake.first_name()
        data.loc[i, 'lastName'] = fake.last_name()
        data.loc[i, 'job'] = fake.job()
        data.loc[i, 'address'] = fake.address()
        data.loc[i, 'city'] = fake.city()
        data.loc[i, 'email'] = fake.email()
    return data


if __name__ == "__main__":
    uri = "mongodb://localhost:27017/kafkacdc"
    client = MongoClient(uri)
    db = client["kafkacdc"]
    collection = db["cdc"]
    data = create_dataframe().to_dict('records')
    collection.insert_many(data)
