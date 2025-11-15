-- Smart Budget App - Seed Categories
-- Migration: V2__seed_categories.sql
-- Description: Inserts predefined categories for income and expenses

-- Insert income categories
INSERT INTO categories (name, type, description) VALUES
('Salary', 'INCOME', 'Regular employment income and wages'),
('Investments', 'INCOME', 'Returns from investments, dividends, and capital gains'),
('Other', 'INCOME', 'Other sources of income');

-- Insert expense categories
INSERT INTO categories (name, type, description) VALUES
('Rent', 'EXPENSE', 'Monthly rent or mortgage payments'),
('Transport', 'EXPENSE', 'Transportation costs including fuel, public transit, and vehicle maintenance'),
('Food', 'EXPENSE', 'Groceries and dining expenses'),
('Entertainment', 'EXPENSE', 'Entertainment, hobbies, and recreational activities'),
('Utilities', 'EXPENSE', 'Electricity, water, gas, internet, and phone bills'),
('Healthcare', 'EXPENSE', 'Medical expenses, insurance, and health-related costs'),
('Shopping', 'EXPENSE', 'Clothing, electronics, and general shopping'),
('Savings', 'EXPENSE', 'Money set aside for savings and emergency funds');
