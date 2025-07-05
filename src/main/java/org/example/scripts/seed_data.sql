INSERT INTO "currency" ("code", "name") VALUES
('USD', 'US Dollar'),
('EUR', 'Euro'),
('GBP', 'British Pound'),
('JPY', 'Japanese Yen'),
('RUB', 'Russian Ruble');

INSERT INTO "organization" ("inn", "ogrn", "adress", "phone", "email", "is_resident", "is_legal_entity", "passport_data", "foreign_tax_id") VALUES
('123456789012', '1234567890123', '100 Main St, New York, USA', '79001234567', 'info@acme.com', true, true, NULL, 'US123456789'),
('234567890123', '2345678901234', '200 Park Ave, London, UK', '79012345678', 'contact@globex.com', true, true, NULL, 'GB987654321'),
('345678901234', '3456789012345', '300 Market St, Berlin, Germany', '79123456789', 'office@wayne.com', true, true, NULL, 'DE456789123'),
('456789012345', '4567890123456', '400 High Tech Rd, Tokyo, Japan', '79234567890', 'support@stark.com', true, true, NULL, 'JP789123456'),
('567890123456', '5678901234567', '500 Silicon Valley, San Francisco, USA', '79345678901', 'hello@oscorp.com', true, true, NULL, 'US987654321'),
('678901234567', '6789012345678', '600 Innovation Dr, Boston, USA', '79456789012', 'contact@umbrella.com', true, true, NULL, 'US654321987'),
('789012345678', '7890123456789', '700 Tech Park, Seoul, South Korea', '79567890123', 'info@cyberdyne.com', true, true, NULL, 'KR321987654'),
('890123456789', '8901234567890', '800 Business Center, Paris, France', '79678901234', 'service@delos.com', true, true, NULL, 'FR987321654'),
('901234567890', '9012345678901', '900 Finance Square, Zurich, Switzerland', '79789012345', 'contact@tyrell.com', true, true, NULL, 'CH654987321'),
('012345678901', '0123456789012', '1000 Industrial Zone, Moscow, Russia', '79890123456', 'info@skynet.com', true, true, NULL, NULL);

INSERT INTO "representative" ("organization_id", "full_name", "position", "phone", "email") VALUES
(1, 'John Smith', 'CEO', '79001112233', 'john.smith@acme.com'),
(1, 'Alice Johnson', 'CFO', '79002223344', 'alice.johnson@acme.com'),
(2, 'Robert Brown', 'Director', '79003334455', 'robert.brown@globex.com'),
(3, 'Emily Davis', 'Manager', '79004445566', 'emily.davis@wayne.com'),
(4, 'Michael Wilson', 'CTO', '79005556677', 'michael.wilson@stark.com'),
(5, 'Sarah Miller', 'Head of Sales', '79006667788', 'sarah.miller@oscorp.com'),
(6, 'David Taylor', 'Legal Counsel', '79007778899', 'david.taylor@umbrella.com'),
(7, 'Jennifer Anderson', 'HR Director', '79008889900', 'jennifer.anderson@cyberdyne.com'),
(8, 'James Thomas', 'Operations Manager', '79009990011', 'james.thomas@delos.com'),
(9, 'Lisa Jackson', 'Marketing Director', '79001001010', 'lisa.jackson@tyrell.com');

INSERT INTO "contract" ("organization_id", "contract_number", "status", "start_date", "end_date", "payment_amount", "currency") VALUES
(1, 'CNT-2023-001', 'ACTIVE', '2023-01-15', '2023-12-31', 100000.00, 'USD'),
(1, 'CNT-2023-002', 'ACTIVE', '2023-02-20', '2024-02-20', 50000.00, 'EUR'),
(2, 'CNT-2023-003', 'SIGNED', '2023-03-10', '2023-09-10', 75000.00, 'GBP'),
(3, 'CNT-2023-004', 'ACTIVE', '2023-04-05', '2024-04-05', 120000.00, 'USD'),
(4, 'CNT-2023-005', 'TERMINATED', '2023-05-12', '2023-08-12', 30000.00, 'JPY'),
(5, 'CNT-2023-006', 'ACTIVE', '2023-06-18', '2023-12-18', 90000.00, 'USD'),
(6, 'CNT-2023-007', 'DRAFT', NULL, NULL, 60000.00, 'EUR'),
(7, 'CNT-2023-008', 'ACTIVE', '2023-08-22', '2024-08-22', 150000.00, 'USD'),
(8, 'CNT-2023-009', 'ACTIVE', '2023-09-30', '2024-03-30', 80000.00, 'GBP'),
(9, 'CNT-2023-010', 'SIGNED', '2023-10-15', '2024-10-15', 200000.00, 'USD');

INSERT INTO "work" ("contract_id", "description", "status", "start_date", "end_date") VALUES
(1, 'Website development', 'IN_PROGRESS', '2023-01-20', '2023-06-20'),
(1, 'Mobile app development', 'PLANNED', '2023-07-01', '2023-12-01'),
(2, 'Cloud migration', 'COMPLETED', '2023-03-01', '2023-08-01'),
(3, 'ERP implementation', 'IN_PROGRESS', '2023-04-15', '2023-11-15'),
(4, 'AI solution development', 'PLANNED', '2023-05-20', '2024-02-20'),
(5, 'IT infrastructure audit', 'CANCELLED', '2023-06-10', '2023-07-10'),
(6, 'Cybersecurity assessment', 'COMPLETED', '2023-07-05', '2023-10-05'),
(7, 'Data analytics platform', 'DRAFT', NULL, NULL),
(8, 'Blockchain integration', 'IN_PROGRESS', '2023-09-01', '2024-05-01'),
(9, 'IoT system deployment', 'PLANNED', '2023-10-10', '2024-02-10');

INSERT INTO "act" ("work_id", "cert_number", "issue_date", "status") VALUES
(1, 'ACT-2023-001', '2023-06-15', 'APPROVED'),
(2, 'ACT-2023-002', '2023-08-10', 'APPROVED'),
(3, 'ACT-2023-003', '2023-11-20', 'PENDING'),
(4, 'ACT-2023-004', '2023-09-30', 'REJECTED'),
(5, 'ACT-2023-005', '2023-12-15', 'DRAFT'),
(6, 'ACT-2023-006', '2023-07-05', 'APPROVED'),
(7, 'ACT-2023-007', '2023-10-01', 'APPROVED'),
(8, 'ACT-2023-008', '2023-11-15', 'PENDING'),
(9, 'ACT-2023-009', '2023-12-20', 'DRAFT'),
(10, 'ACT-2023-010', '2024-01-10', 'PENDING');