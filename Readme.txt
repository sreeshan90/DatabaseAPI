Multi-dimensional search: 

Consider a retailer like Amazon. They carry tens of thousands of products, and each product has many
attributes (Name, Size, Description, Keywords, Manufacturer, Price, etc.).
Their web site allows users to specify attributes of products that
they are seeking, and displays products that have most of those attributes.
To make search efficient, the data is organized using appropriate
data structures, such as balanced trees and hashing.  But, if products
are organized by Name, how can search by price be implemented efficiently?
The solution, called indexing in databases, is to create a new set of
references to the objects for each search field, and organize them to
implement search operations on that field efficiently.
As the objects change, these access structures have to be kept consistent. 

In this project, each customer object has 3 attributes: id (long int),
categories (one or more integers 1-999), and amount (dollars and cents).
The amount field stores the total revenue that the customer has generated
for our company.  The categories field is a set of department codes of
our company (encoded as an integer in [1,999]) that stock items of interest
to the customer.  Customers are uniquely identified by their id (key field).  

The project has implementation of certain rudimentary operations that are carried out 
in a better running time with the use of appropriate data structures.

Compilation:

javac DatabaseAPI.java


Execution:

java DatabaseAPI p3-s3-l.txt

java DatabaseAPI p3-s3-k.txt

java DatabaseAPI p3-s3-d.txt

java DatabaseAPI p3-s3-ck.txt
