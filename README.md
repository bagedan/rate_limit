# rate_limit

This app will load api keys conf from "env".properties. So if you run it locally, specify -Denv=local in VM options.

## Available api

GET /hotels
Required header: Authorization
Required parameters: cityId
Optinal parameters: order = [asc|desc]

Ex: localhost:8080/hotels?cityId=Bangkok&order=desc

## Design details

- api key validator is integrated as interceptor in spring mvc and will return 404 in any case of failure. it's done due to not expose internal details too much.
If this api available only internally, then we could change it to more descriptive statuses.

- api key validator has a potential memory leak - it keep last used time for all api keys, never cleaning that map.
Obviously in prod we might have unlimited number of apikey as they got generated and it will cause a problem. But in prod I would never try to implement cache by myself :)

