-- Story 4.1 - Categorization rules table and seed data
CREATE TABLE IF NOT EXISTS categorization_rules (
    id BIGSERIAL PRIMARY KEY,
    keyword VARCHAR(120) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('INCOME', 'EXPENSE')),
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_categorization_rules_transaction_type
    ON categorization_rules (transaction_type);
CREATE INDEX IF NOT EXISTS idx_categorization_rules_keyword
    ON categorization_rules (LOWER(keyword));

-- Helper function to insert keyword mapped to a category by name
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'starbucks', 'EXPENSE', id FROM categories WHERE name = 'Food' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'coffee', 'EXPENSE', id FROM categories WHERE name = 'Food' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'restaurant', 'EXPENSE', id FROM categories WHERE name = 'Food' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'dinner', 'EXPENSE', id FROM categories WHERE name = 'Food' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'lunch', 'EXPENSE', id FROM categories WHERE name = 'Food' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'groceries', 'EXPENSE', id FROM categories WHERE name = 'Food' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'supermarket', 'EXPENSE', id FROM categories WHERE name = 'Food' LIMIT 1;

INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'uber', 'EXPENSE', id FROM categories WHERE name = 'Transport' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'lyft', 'EXPENSE', id FROM categories WHERE name = 'Transport' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'taxi', 'EXPENSE', id FROM categories WHERE name = 'Transport' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'gas', 'EXPENSE', id FROM categories WHERE name = 'Transport' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'fuel', 'EXPENSE', id FROM categories WHERE name = 'Transport' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'bus', 'EXPENSE', id FROM categories WHERE name = 'Transport' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'train', 'EXPENSE', id FROM categories WHERE name = 'Transport' LIMIT 1;

INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'rent', 'EXPENSE', id FROM categories WHERE name = 'Rent' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'mortgage', 'EXPENSE', id FROM categories WHERE name = 'Rent' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'landlord', 'EXPENSE', id FROM categories WHERE name = 'Rent' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'lease', 'EXPENSE', id FROM categories WHERE name = 'Rent' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'apartment', 'EXPENSE', id FROM categories WHERE name = 'Rent' LIMIT 1;

INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'salary', 'INCOME', id FROM categories WHERE name = 'Salary' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'paycheck', 'INCOME', id FROM categories WHERE name = 'Salary' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'payroll', 'INCOME', id FROM categories WHERE name = 'Salary' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'bonus', 'INCOME', id FROM categories WHERE name = 'Salary' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'wages', 'INCOME', id FROM categories WHERE name = 'Salary' LIMIT 1;

INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'electricity', 'EXPENSE', id FROM categories WHERE name = 'Utilities' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'water bill', 'EXPENSE', id FROM categories WHERE name = 'Utilities' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'internet', 'EXPENSE', id FROM categories WHERE name = 'Utilities' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'wifi', 'EXPENSE', id FROM categories WHERE name = 'Utilities' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'phone bill', 'EXPENSE', id FROM categories WHERE name = 'Utilities' LIMIT 1;

INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'movie', 'EXPENSE', id FROM categories WHERE name = 'Entertainment' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'netflix', 'EXPENSE', id FROM categories WHERE name = 'Entertainment' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'concert', 'EXPENSE', id FROM categories WHERE name = 'Entertainment' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'game', 'EXPENSE', id FROM categories WHERE name = 'Entertainment' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'spotify', 'EXPENSE', id FROM categories WHERE name = 'Entertainment' LIMIT 1;

INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'pharmacy', 'EXPENSE', id FROM categories WHERE name = 'Healthcare' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'doctor', 'EXPENSE', id FROM categories WHERE name = 'Healthcare' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'hospital', 'EXPENSE', id FROM categories WHERE name = 'Healthcare' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'clinic', 'EXPENSE', id FROM categories WHERE name = 'Healthcare' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'insurance', 'EXPENSE', id FROM categories WHERE name = 'Healthcare' LIMIT 1;

INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'amazon', 'EXPENSE', id FROM categories WHERE name = 'Shopping' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'mall', 'EXPENSE', id FROM categories WHERE name = 'Shopping' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'retail', 'EXPENSE', id FROM categories WHERE name = 'Shopping' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'clothes', 'EXPENSE', id FROM categories WHERE name = 'Shopping' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'electronics', 'EXPENSE', id FROM categories WHERE name = 'Shopping' LIMIT 1;

INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'dividend', 'INCOME', id FROM categories WHERE name = 'Investments' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'stock', 'INCOME', id FROM categories WHERE name = 'Investments' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'interest', 'INCOME', id FROM categories WHERE name = 'Investments' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'capital gain', 'INCOME', id FROM categories WHERE name = 'Investments' LIMIT 1;

INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'savings transfer', 'EXPENSE', id FROM categories WHERE name = 'Savings' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'emergency fund', 'EXPENSE', id FROM categories WHERE name = 'Savings' LIMIT 1;
INSERT INTO categorization_rules (keyword, transaction_type, category_id)
SELECT 'rainy day', 'EXPENSE', id FROM categories WHERE name = 'Savings' LIMIT 1;
