
##### The project files are uploaded to master branch
#### Update : I have improved the MVP with Beautiful Math Rendering (LaTeX) and added Questions from the PYQs in the Database. Below is the link:
https://olympiadedgeaisigned.s3.ap-south-1.amazonaws.com/app-release1.apk

# OlympiadEdgeAI - A Socratic AI Math Tutor

**Submission for the AI for Bharat Hackathon.**

OlympiadEdgeAI is a mobile-first Android application designed to revolutionize how students prepare for competitive math olympiads. Instead of providing instant answers, our app features an AI-powered tutor that uses the **Socratic method**—guiding students with step-by-step questions and hints to help them arrive at the solution on their own.


##  The Problem

Top-tier math competitions require deep conceptual understanding, not just rote memorization. However, most digital learning tools simply provide final answers, which hinders the development of problem-solving skills. Personalized, one-on-one Socratic tutoring is incredibly effective but is often expensive and inaccessible to the majority of students in Bharat.

##  Our Solution

OlympiadEdgeAI bridges this gap by providing a personal, AI-powered Socratic tutor on every student's device. Our tutor doesn't give away the solution; it fosters critical thinking by asking guiding questions and providing context-aware hints that respect the student's learning journey.

### Key Features

*   **Curated Question Bank:** A list of olympiad-style math problems categorized by difficulty and topic (Algebra, Geometry, etc.).
*   **Socratic AI Tutor:** A sophisticated backend powered by Amazon Bedrock that "remembers" the conversation and provides intelligent, progressive hints.
*   **Stateful Conversation:** The AI Tutor has memory of the current session, providing relevant follow-up hints instead of repeating itself.
*   **Offline Practice Mode:** Users can still view questions and practice even without an internet connection, making learning accessible everywhere.

## Tech Stack & Architecture

We have built a robust, scalable, and modern full-stack application leveraging the power of AWS and native Android development.

*   **Frontend (Android Application):**
    *   **Language:** Java
    *   **Architecture:** MVVM (ViewModel, LiveData, Repository)
    *   **Database:** Room for local caching of questions and user attempts.
    *   **Networking:** Retrofit & OkHttp for efficient API communication.
    *   **UI:** AndroidX components, RecyclerView, and WebView for rendering LaTeX.

*   **Backend (Serverless on AWS):**
    *   **API Layer:** **Amazon API Gateway** to provide a secure and scalable REST endpoint.
    *   **Compute Layer:** **AWS Lambda** (Python) to orchestrate the AI logic.
    *   **AI Model:** **Amazon Bedrock** using the powerful `amazon.nova-pro-v1:0` model for complex reasoning.
    *   **Database:** **Amazon DynamoDB** for storing user progress and conversation state.

### System Flow
Android App
↓
API Gateway
↓
AWS Lambda
↓
Amazon Bedrock (LLM)
↓
AI Tutor Response


### Components

#### Android Application
- Java
- RecyclerView based question interface
- Chat-based tutor interaction
- Room Database for local question storage

#### Backend
- AWS Lambda (Python)
- REST API using API Gateway
- Prompt orchestration

#### AI Layer
- Amazon Bedrock
- Amazon Nova Pro LLM
- Structured tutoring prompts

---

## Socratic Tutoring Method

Instead of giving answers, the tutor:

1. Understands the problem
2. Guides the student with hints
3. Encourages step-by-step reasoning
4. Avoids revealing the final answer immediately

### Example Interaction

**Student**

> I'm stuck.

**Tutor**

> What equation represents the perimeter using the sides 13, 13, and x?

---

## Example Problem

**Problem**

An isosceles triangle has a perimeter of **32 cm**.  
The equal sides are **13 cm each**.  
Find the third side.

**Tutor Guidance**

Step 1  
What equation represents the perimeter?

Step 2  
What is **13 + 13**?

Step 3  
If **26 + x = 32**, what must **x** be?

## Example Interaction

### Problem

If \(x + \frac{1}{x} = 3\), find the value of \(x^2 + \frac{1}{x^2}\).

### Tutor Interaction (Socratic Method)

**Student**

> I'm stuck. Can you give me a hint?

**Tutor**

> What happens if you square the expression \(x + \frac{1}{x}\)?

**Student**

> \((x + \frac{1}{x})^2 = x^2 + 2 + \frac{1}{x^2}\)

**Tutor**

> Good. Now substitute the value \(x + \frac{1}{x} = 3\). What do you get?

**Student**

> \(9 = x^2 + 2 + \frac{1}{x^2}\)

**Tutor**

> Great. What is the value of \(x^2 + \frac{1}{x^2}\)?

**Student**

> \(7\)

### Learning Outcome

Instead of revealing the answer immediately, the tutor helps the student:

- Recall an algebraic identity  
- Apply the identity to the given expression  
- Solve the problem step-by-step  

This encourages **conceptual understanding and independent reasoning**, which is essential for **Olympiad mathematics**.

---



## Database

Problems are stored locally using **Room Database**.

Each question contains:

- Topic
- Difficulty
- Problem statement
- Answer
- Hint
- Optional diagram URL

---

## Installation

Clone the repository:

```bash
git clone https://github.com/yourusername/olympiadedge-ai.git
