1. Basic String Compression (Run-Length Encoding) Example: Input: aaabbc → Output: a3b2c1
2. Ignore Single Count Example: Input: aaabbc → Output: a3b2c
3. Case-Insensitive Compression Example: Input: aAaBBb → Output: a3b3
4. Compression with Order Preserved (Using Map) Example: Input: banana → Output: b1a3n2
5. In-Place Compression (LeetCode Type) Example: Input: ['a','a','b','b','c','c','c'] → Output:
['a','2','b','2','c','3']
6. Compression with Maximum 2 Repeats Example: Input: aaabbbcc → Output: aabbcc
7. Decode Compressed String Example: Input: a3b2c1 → Output: aaabbc
8. Nested Compression (Advanced) Example: Input: 3[a2[c]] → Output: accaccacc
9. Compression with Index Tracking Example: Input: aaabb → Output: [(a,3,0), (b,2,3)]
10. Remove Adjacent Duplicates Example: Input: abbaca → Output: ca
11. K-Group Removal Compression Example: Input: deeedbbcccbdaa, k=3 → Output: aa
12. Longest Compressed Length Return only the length of compressed string
13. Compression with Special Characters Example: Input: aa!! bb → Output: a2!2 2b2
14. Streaming Compression Compress characters coming from stream one by one
15. Compression with Sorting Example: Input: cbbaa → Sorted: aabbc → Output: a2b2c1
