-- Add RESERVED value to book_status enum
ALTER TYPE book_status ADD VALUE IF NOT EXISTS 'RESERVED';
