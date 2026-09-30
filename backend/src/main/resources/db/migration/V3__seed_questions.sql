INSERT INTO questions
    (id, track, difficulty, topic, prompt, question_type, coding_language, starter_code, function_signature,
     hidden_test_cases, created_at, updated_at)
VALUES
('10000000-0000-0000-0000-000000000001', 'DSA', 'EASY', 'Arrays',
 'Given an array of integers, find the maximum value in one pass.',
 'CODING', 'Java', 'class Solution {
    public int solve(int[] values) {
        return 0;
    }
}', 'int solve(int[] values)', '{"cases": [{"input": "[3,1,4]", "output": "4"}]}',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000002', 'DSA', 'MEDIUM', 'Hash Maps',
 'Explain and implement a method that returns the first non-repeating character in a string.',
 'CODING', 'Java', 'class Solution {
    public char solve(String value) {
        return 0;
    }
}', 'char solve(String value)', '{"cases": [{"input": "\"swiss\"", "output": "\"w\""}]}',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000003', 'DSA', 'HARD', 'Graphs',
 'Design an algorithm to find the shortest path in a weighted graph with non-negative edges.',
 'CODING', 'Java', 'class Solution {
    public int[] shortestPath(int[][] edges, int vertices, int source) {
        return new int[0];
    }
}', 'int[] shortestPath(int[][] edges, int vertices, int source)',
 '{"cases": [{"input": "graph", "output": "distances"}]}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000004', 'BEHAVIORAL', 'EASY', 'Communication',
 'Tell me about a time you had to explain a technical idea to a non-technical audience.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000005', 'BEHAVIORAL', 'MEDIUM', 'Conflict Resolution',
 'Describe a disagreement with a teammate and how you reached a productive outcome.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000006', 'BEHAVIORAL', 'HARD', 'Leadership',
 'Describe a situation where you had to make a difficult decision with incomplete information.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000007', 'SYSTEM_DESIGN', 'EASY', 'APIs',
 'What makes an HTTP API predictable and easy for clients to use?',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000008', 'SYSTEM_DESIGN', 'MEDIUM', 'Caching',
 'Design a caching strategy for a read-heavy product catalog.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000009', 'SYSTEM_DESIGN', 'HARD', 'Scalability',
 'Design a globally available notification service and discuss its failure modes.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000010', 'OOP', 'EASY', 'Encapsulation',
 'Explain encapsulation and give an example of protecting an object invariant.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000011', 'OOP', 'MEDIUM', 'Design Patterns',
 'When would you choose composition over inheritance? Explain the trade-offs.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000012', 'OOP', 'HARD', 'Concurrency',
 'Design a thread-safe in-memory component and explain its consistency guarantees.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000013', 'JAVA', 'EASY', 'Collections',
 'What is the difference between ArrayList and LinkedList, and when would you use each?',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000014', 'JAVA', 'MEDIUM', 'Streams',
 'Write a Java Stream pipeline that groups orders by customer and sums their totals.',
 'CODING', 'Java', 'class Solution {
    public Map<String, Integer> totals(List<Order> orders) {
        return Map.of();
    }
}', 'Map<String, Integer> totals(List<Order> orders)',
 '{"cases": [{"input": "orders", "output": "totals by customer"}]}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000015', 'JAVA', 'HARD', 'JVM',
 'Explain how garbage collection affects latency and how you would investigate a pause.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000016', 'DSA', 'EASY', 'Strings',
 'Given a string, determine whether it is a palindrome.',
 'CODING', 'Java', 'class Solution {
    public boolean isPalindrome(String value) {
        return false;
    }
}', 'boolean isPalindrome(String value)', '{"cases": [{"input": "\"level\"", "output": "true"}]}',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000017', 'DSA', 'MEDIUM', 'Stacks',
 'Evaluate an expression containing parentheses and explain the stack invariant.',
 'CODING', 'Java', 'class Solution {
    public int evaluate(String expression) {
        return 0;
    }
}', 'int evaluate(String expression)', '{"cases": [{"input": "\"2*(3+4)\"", "output": "14"}]}',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000018', 'DSA', 'HARD', 'Dynamic Programming',
 'Find the minimum number of coins needed to make a target amount.',
 'CODING', 'Java', 'class Solution {
    public int minCoins(int[] coins, int amount) {
        return 0;
    }
}', 'int minCoins(int[] coins, int amount)', '{"cases": [{"input": "[1,2,5],11", "output": "3"}]}',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000019', 'BEHAVIORAL', 'EASY', 'Motivation',
 'What interests you about this role and how do you keep learning?',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000020', 'BEHAVIORAL', 'MEDIUM', 'Adaptability',
 'Tell me about a time requirements changed late and how you adapted.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000021', 'BEHAVIORAL', 'HARD', 'Influence',
 'Describe how you gained support for a technical decision you did not own.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000022', 'SYSTEM_DESIGN', 'EASY', 'Databases',
 'When would you choose a relational database over a document database?',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000023', 'SYSTEM_DESIGN', 'MEDIUM', 'Queues',
 'Explain how a message queue can help decouple two services.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000024', 'SYSTEM_DESIGN', 'HARD', 'Reliability',
 'Design a service that remains useful during a dependency outage.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000025', 'OOP', 'EASY', 'Polymorphism',
 'Explain polymorphism with a small Java example.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000026', 'OOP', 'MEDIUM', 'SOLID',
 'Explain the dependency inversion principle and a practical benefit.',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000027', 'OOP', 'HARD', 'Architecture',
 'How would you evolve a tightly coupled object model without breaking clients?',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000028', 'JAVA', 'EASY', 'Exceptions',
 'What is the difference between checked and unchecked exceptions?',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000029', 'JAVA', 'MEDIUM', 'Concurrency',
 'Implement a bounded producer-consumer queue using standard Java concurrency tools.',
 'CODING', 'Java', 'class Solution {
    public void runQueue() {
    }
}', 'void runQueue()', '{"cases": [{"input": "producer-consumer", "output": "all items processed"}]}',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('10000000-0000-0000-0000-000000000030', 'JAVA', 'HARD', 'Performance',
 'How would you diagnose and improve a slow Java service without guessing?',
 'NON_CODING', NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
