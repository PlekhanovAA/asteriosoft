<h1>Test task</h1>

Back-end parts for REST interaction with a web application that manages advertising categories and text banners.
To run, you will need <strong>Docker</strong> and clone the repository into the working directory.

<h3>Preparation and launch</h3>

1. Download the database <strong>MySQL</strong> image:
```
docker pull mysql/mysql-server:8.0.32
```
2. Create a network <em>springmysql-net</em> to establish communication between containers:
```
docker network create -d bridge springmysql-net
```
3. Launch a container <em>mysql-docker-container</em> based on the downloaded DB image:
```
docker run --name=mysql-docker-container --network=springmysql-net -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=mysql -e MYSQL_DATABASE=test_db -e MYSQL_USER=mysql -e MYSQL_PASSWORD=mysql mysql/mysql-server:8.0.32
```
4. Build an image <em>asteriosoft-test-docker-container</em> from the working directory:
```
docker build -t asteriosoft-test-docker-container .
```
5. Launch a container <em>asteriosoft-test-docker-container</em> based on the previously built image:
```
docker run --name=asteriosoft-test-docker-container --network=springmysql-net -d -p 8080:8080 -e DATABASE_HOST=mysql-docker-container -e DATABASE_USER=mysql -e DATABASE_PORT=3306 -e DATABASE_PASSWORD=mysql -e DATABASE_NAME=test_db asteriosoft-test-docker-container
```
Preparation and launch are finished. The API is available by <em>localhost:8080</em>. For testing, you can use an HTTP client, for example, <strong>Potsman</strong>.
When the container <em>asteriosoft-test-docker-container</em> is first started, some technical records are added to the database.
Therefore, available, for example:
- users: <em>user/pass</em> and <em>admin/password</em>
- categories: <em>category1</em>, <em>category2</em>
- banners: <em>banner1</em>, <em>banner2</em>
- as well as some links between these categories and banners

Below are examples of endpoints.

<h3>Available public endpoints</h3>

```
localhost:8080/bid?cat=cat_request_id_1&cat=cat_request_id_2 
```
A <strong>GET</strong> endpoint will return a banner from one category, the ID of which is specified in the request.
The result will look like this:
```json
{
    "id": 2,
    "name": "banner2",
    "price": 100.50,
    "isDeleted": false,
    "categories": [
        {
            "id": 1,
            "name": "category1",
            "requestId": "cat_request_id_1",
            "isDeleted": false
        }
    ]
}
```
***
```
localhost:8080/login
```
A <strong>POST</strong> endpoint will return the <em>Authorization</em> header, whose value <u>should be passed in requests as a header <em>Authorization</em> value to private endpoints</u>.
For successful authorization, the <em>Content-Type: application/x-www-form-urlencoded</em> header must be set in the request and the username/password must be passed in the request body.

<h3>Available private endpoints</h3>

```
localhost:8080/banners
```
A <strong>GET</strong> endpoint will return all the banners that have not been deleted.
***
```
localhost:8080/banners/filter?searchText=r1
```
A <strong>GET</strong> endpoint will return all the banners that have not been deleted and contain in the name the text from the <em>searchText</em> request parameter.
***
```
localhost:8080/banner
```
A <strong>POST</strong> endpoint will create a new banner. In the request body, you must pass JSON with required fields and correct values, for example, such:
```json
{
  "name": "new banner",
  "price": 100500,
  "categories": ["category1", "category2"]
}
```
***
```
localhost:8080/banner/3
```
A <strong>POST</strong> endpoint will update the existing banner with ID=3. In the request body, you must pass JSON with required fields and correct values.
***
```
localhost:8080/banner/3
```
A <strong>GET</strong> endpoint will remove the existing banner with ID=3.
***
```
localhost:8080/categories
```
A <strong>GET</strong> endpoint will return all the categories that have not been deleted.
***
```
localhost:8080/categories/filter?searchText=r1
```
A <strong>GET</strong> endpoint will return all the categories that have not been deleted and contain in the name the text from the <em>searchText</em> request parameter.
***
```
localhost:8080/category
```
A <strong>POST</strong> endpoint will create a new category. In the request body, you must pass JSON with required fields and correct values, for example, such:
```json
{
  "name": "new category",
  "requestId": "cat_request_id_new_category"
}
```
***
```
localhost:8080/category/3
```
A <strong>POST</strong> endpoint will update the existing category with ID=3. In the request body, you must pass JSON with required fields and correct values.
***
```
localhost:8080/category/3
```
A <strong>GET</strong> endpoint will remove the existing category with ID=3.