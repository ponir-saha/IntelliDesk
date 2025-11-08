-- Initialize IntelliDesk Database Schema

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create schemas for multi-tenancy
CREATE SCHEMA IF NOT EXISTS public;

-- Users and Authentication tables will be created by user-service
-- Notifications tables will be created by notification-service
-- Each service manages its own schema

-- Create a default admin user (password will be hashed by the application)
-- This is just a placeholder, actual user creation should be done through the application
