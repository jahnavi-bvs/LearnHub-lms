-- LearnHub course catalog: 15 published courses, each owned by a named instructor.
--
-- Before running: the instructor accounts below must exist, and course-service must have started
-- once with the category/level code so the columns exist.
-- Safe to run more than once: a course that instructor already has is skipped.
-- Run with UTF-8 so the dash in the first title survives:
--   mysql -u learnhub -p --default-character-set=utf8mb4 < backend/seed/courses.sql

INSERT INTO learnhub_course.courses
    (title, description, category, level, instructor_id, instructor_name, published, created_at, updated_at)
SELECT c.title, c.description, c.category, c.level, u.id, u.full_name, TRUE,
       -- Catalog lists newest first: each course is 1 second "older", so course 1 comes first
       TIMESTAMPADD(SECOND, -c.position, UTC_TIMESTAMP(6)), TIMESTAMPADD(SECOND, -c.position, UTC_TIMESTAMP(6))
FROM (
    SELECT 1 AS position, 'instructor1@learnhub.com' AS email, 'Java Programming – Beginner to Advanced' AS title,
           'Programming' AS category, 'Beginner' AS level,
           'Learn Java fundamentals, OOP, collections, exception handling and JDBC.' AS description
    UNION ALL SELECT 2, 'instructor2@gmail.com', 'Full Stack Web Development', 'Web Development', 'Intermediate',
           'HTML, CSS, JavaScript, React, Node.js and REST APIs.'
    UNION ALL SELECT 3, 'instructor1@learnhub.com', 'Python Programming', 'Programming', 'Beginner',
           'Python basics, functions, OOP, libraries and practical projects.'
    UNION ALL SELECT 4, 'instructor2@gmail.com', 'Database Management Systems', 'Database', 'Intermediate',
           'SQL, MySQL, normalization, indexing, transactions and database design.'
    UNION ALL SELECT 5, 'instructor3@learnhub.com', 'Artificial Intelligence & Machine Learning', 'AI & ML', 'Advanced',
           'Machine learning algorithms, neural networks and AI applications.'
    UNION ALL SELECT 6, 'instructor3@learnhub.com', 'Cybersecurity Fundamentals', 'Security', 'Beginner',
           'Network security, encryption, authentication and security fundamentals.'
    UNION ALL SELECT 7, 'instructor3@learnhub.com', 'Cloud Computing Fundamentals', 'Cloud Technology', 'Beginner',
           'Cloud concepts, AWS, virtualization and cloud deployment.'
    UNION ALL SELECT 8, 'instructor4@learnhub.com', 'Principles of Business Management', 'Commerce', 'Beginner',
           'Business organization, management principles and decision-making.'
    UNION ALL SELECT 9, 'instructor5@learnhub.com', 'Financial Accounting Fundamentals', 'Accounting', 'Beginner',
           'Journal entries, ledgers, trial balance, balance sheets and profit & loss statements.'
    UNION ALL SELECT 10, 'instructor5@learnhub.com', 'Financial Management', 'Finance', 'Intermediate',
           'Budgeting, financial planning, investment decisions and risk management.'
    UNION ALL SELECT 11, 'instructor6@learnhub.com', 'Human Anatomy & Physiology', 'Biology', 'Beginner',
           'Human body systems, organs, tissues and their functions.'
    UNION ALL SELECT 12, 'instructor7@learnhub.com', 'Introduction to Genetics', 'Biology', 'Intermediate',
           'DNA, genes, chromosomes, inheritance and genetic variation.'
    UNION ALL SELECT 13, 'instructor8@learnhub.com', 'Fundamentals of Physics', 'Physics', 'Beginner',
           'Mechanics, motion, energy, electricity and magnetism.'
    UNION ALL SELECT 14, 'instructor8@learnhub.com', 'Applied Mathematics', 'Mathematics', 'Intermediate',
           'Algebra, calculus, statistics and practical mathematical applications.'
    UNION ALL SELECT 15, 'instructor8@learnhub.com', 'Graphic Design Fundamentals', 'Design', 'Beginner',
           'Design principles, typography, color theory and digital design tools.'
) AS c
JOIN learnhub_auth.users u ON u.email = c.email AND u.role = 'INSTRUCTOR'
WHERE NOT EXISTS (
    SELECT 1 FROM learnhub_course.courses existing
    WHERE existing.title = c.title AND existing.instructor_id = u.id
);
