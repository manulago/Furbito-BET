---
description: How to deploy FurbitoBET online for free
---

# Free Online Deployment Guide

This guide describes how to deploy FurbitoBET online for free using the following stack:
- **Database**: [Neon](https://neon.tech) (Free Serverless PostgreSQL)
- **Backend**: [Render](https://render.com) (Free Web Service)
- **Frontend**: [Vercel](https://vercel.com) (Free Static Site Hosting)

## Prerequisites
- A GitHub account.
- The project code pushed to a GitHub repository.

## Step 1: Database (Neon)

1.  Go to [Neon.tech](https://neon.tech) and sign up.
2.  Create a new project.
3.  Copy the **Connection String** (Postgres URL). It looks like `postgres://user:pass@ep-xyz.aws.neon.tech/neondb`.
    *   *Note: You will need this for the Backend configuration.*

## Step 2: Backend (Render)

1.  Go to [Render.com](https://render.com) and sign up.
2.  Click **New +** -> **Web Service**.
3.  Connect your GitHub repository.
4.  Select the `backend` directory as the **Root Directory** (if asked, or configure build context).
    *   **Runtime**: Docker
    *   **Build Context**: `backend` (Important! Point this to the backend folder)
    *   **Dockerfile Path**: `Dockerfile`
5.  Scroll down to **Environment Variables** and add:
    *   `DB_URL`: Paste your Neon connection string. **IMPORTANT**: Append `?sslmode=require` if it's not there.
        *   Example: `jdbc:postgresql://ep-xyz...neon.tech/neondb?sslmode=require`
        *   *Note: Since the app uses JDBC, ensure the format is `jdbc:postgresql://...`*
    *   `DB_USER`: (Extract from Neon URL, usually inside the string)
    *   `DB_PASSWORD`: (Extract from Neon URL)
    *   `MAIL_USERNAME`: Your Gmail address.
    *   `MAIL_PASSWORD`: Your Gmail App Password.
    *   `PORT`: `8080`
6.  Click **Create Web Service**.
7.  Wait for the build to finish. Once deployed, copy the **Service URL** (e.g., `https://furbitobet-backend.onrender.com`).

## Step 3: Frontend (Vercel)

1.  Go to [Vercel.com](https://vercel.com) and sign up.
2.  Click **Add New...** -> **Project**.
3.  Import your GitHub repository.
4.  Configure the project:
    *   **Framework Preset**: Vite
    *   **Root Directory**: Click `Edit` and select `frontend`.
    *   **Environment Variables**:
        *   `VITE_API_URL`: Paste your Render Backend URL (e.g., `https://furbitobet-backend.onrender.com`). **Do not add a trailing slash**.
5.  Click **Deploy**.

## Summary of URLs

-   **Frontend**: `https://your-project.vercel.app` (Share this with users)
-   **Backend**: `https://your-project.onrender.com`
-   **Database**: Hosted on Neon.

> [!NOTE]
> The Render free tier spins down after 15 minutes of inactivity. The first request after inactivity may take 50+ seconds to load.
