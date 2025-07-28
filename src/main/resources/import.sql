INSERT INTO banks (code, name) VALUES
  ('000', 'Banco Vazio'),
  ('999', 'Banco Cheio');

INSERT INTO tags (name, color) VALUES
  ('Tag existe 1', 1234),
  ('Tag existe 2', 3216);

INSERT INTO accounts (name, account_type, bank_id, branch, account_number, credit_limit) VALUES
  ('Conta Corrente Exemplo', 'CHECKING', 1, '0001', '0001', 1000000),
  ('Conta Poupança Exemplo', 'SAVINGS' , 1, '0001', '0002', 0),
  ('Conta Investimento Exemplo', 'INVEST' , null, null, null, 0);

INSERT INTO provisions (name, initial_date, final_date, amount) VALUES
  ('Receita 1', '2025-01-01 12:00:00+00', NULL, 10000),
  ('Despesa 4', '2025-01-15 16:00:00+00', NULL, 1000);

INSERT INTO histories (name, payment_date, amount, account_id) VALUES
  ('Histórico 1', '2025-01-01 12:00:00+00', 10000, 1),
  ('Histórico 2', '2025-01-10 17:00:00+00', -5000, 1),
  ('Histórico 3', '2025-01-11 13:00:00+00', -2000, 3),
  ('Histórico 4', '2025-01-15 16:00:00+00', -1000, null);

INSERT INTO histories_tags (history_id, tag_id) VALUES
 (1, 1),
 (2, 1), (2, 2),
 (3, 1),
 (4, 1), (4, 2);
