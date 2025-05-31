CSV

curl -X POST -H "Content-Type: application/json" \
-d '[{"nombre":"Producto Alfa","cantidad":10,"precio":15.99,"total":159.90},{"nombre":"Producto Beta","cantidad":5,"precio":100.00,"total":500.00}]' \
--output reporte_generado.csv \
http://localhost:8080/api/reports/csv-from-json


PDF

curl -X POST -H "Content-Type: application/json" \
-d '[{"nombre":"Producto Alfa","cantidad":10,"precio":15.99,"total":159.90},{"nombre":"Producto Beta","cantidad":5,"precio":100.00,"total":500.00}]' \
--output reporte_generado.pdf \
http://localhost:8080/api/reports/pdf-from-json


xlsx

curl -X POST -H "Content-Type: application/json" \
-d '[{"nombre":"Producto Alfa","cantidad":10,"precio":15.99,"total":159.90},{"nombre":"Producto Beta","cantidad":5,"precio":100.00,"total":500.00}]' \
--output reporte_generado.xlsx \
http://localhost:8080/api/reports/excel-from-json
