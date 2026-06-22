# Roast My Resume 🔥

An AI-powered resume analyzer that matches your resume against job descriptions using **Spring AI** and **Ollama** for local LLM processing.

## Features

- 📄 **PDF Resume Parsing** - Upload resumes in PDF format
- 🤖 **AI Analysis** - Uses Ollama (local LLM) for intelligent analysis
- 📊 **Detailed Feedback** - Get match scores, strengths, improvements, and missing skills
- 🚀 **Simple REST API** - Easy integration with frontend applications

## Architecture

```
Spring Boot Application
    ├── ResumeParserService: Extracts text from PDF files
    ├── ResumeMatcher: Analyzes resumes using Spring AI + Ollama
    └── AnalysisController: REST endpoints for the API
```

## Prerequisites

1. **Java 17+** - Install from [oracle.com](https://www.oracle.com/java/technologies/downloads/)
2. **Maven** - Install from [maven.apache.org](https://maven.apache.org/)
3. **Ollama** - Install from [ollama.ai](https://ollama.ai)
4. **Ollama Model** - Pull a model (e.g., `mistral` or `llama2`)

## Setup Instructions

### 1. Install and Run Ollama

```bash
# Install Ollama from https://ollama.ai

# Pull the mistral model (or your preferred model)
ollama pull mistral

# Start Ollama (it will run on http://localhost:11434)
ollama serve
```

### 2. Build and Run the Application

```bash
# Clone the repository
git clone https://github.com/ClassHacker/roast-my-resume.git
cd roast-my-resume

# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Analyze Resume

**POST** `/api/analyze`

Analyzes a resume against a job description.

**Request:**
- **Form Data:**
  - `resumeFile` (file, required): PDF resume file
  - `jobDescription` (text, required): Job description text

**Response:**
```json
{
  "matchScore": 75.5,
  "feedback": "Your resume aligns well with the position...",
  "strengths": "Strong Java background, 5+ years experience...",
  "improvements": "Consider adding cloud certifications...",
  "missingSkills": "Kubernetes, Docker, AWS..."
}
```

**Example using cURL:**
```bash
curl -X POST http://localhost:8080/api/analyze \
  -F "resumeFile=@resume.pdf" \
  -F "jobDescription=<job_description_text>"
```

**Example using Python:**
```python
import requests

with open('resume.pdf', 'rb') as f:
    files = {'resumeFile': f}
    data = {
        'jobDescription': 'Your job description here...'
    }
    response = requests.post(
        'http://localhost:8080/api/analyze',
        files=files,
        data=data
    )
    print(response.json())
```

### Health Check

**GET** `/api/analyze/health`

Checks if the service is running.

**Response:**
```
Resume Analyzer is running!
```

## Configuration

Edit `src/main/resources/application.yml` to customize:

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434  # Ollama server URL
      model: mistral                     # LLM model to use
```

**Available Ollama Models:**
- `mistral` - Fast, good quality (recommended)
- `llama2` - More accurate, slower
- `neural-chat` - Conversational
- `dolphin-mixtral` - Highly capable

Pull additional models:
```bash
ollama pull llama2
ollama pull neural-chat
```

## Project Structure

```
roast-my-resume/
├── src/
│   ├── main/
│   │   ├── java/com/roastmyresume/
│   │   │   ├── controller/
│   │   │   │   └── AnalysisController.java
│   │   │   ├── service/
│   │   │   │   ├── ResumeParserService.java
│   │   │   │   └── ResumeMatcher.java
│   │   │   ├── model/
│   │   │   │   ├── Resume.java
│   │   │   │   └── AnalysisResult.java
│   │   │   └── RoastMyResumeApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
├── pom.xml
└── README.md
```

## Troubleshooting

### "Connection refused" error
- Ensure Ollama is running: `ollama serve`
- Check if running on correct port: `http://localhost:11434`

### "Model not found" error
- Pull the model: `ollama pull mistral`
- Verify in `application.yml` the model name matches

### PDF parsing errors
- Ensure PDF is not encrypted
- Try a different PDF file
- Check file is actually a PDF (not renamed)

### Slow responses
- First request may be slow while LLM loads
- Subsequent requests should be faster
- Try a lighter model like `mistral` instead of `llama2`

## Technologies Used

- **Spring Boot 3.2.0** - Application framework
- **Spring AI 0.8.1** - AI integration
- **Ollama** - Local LLM provider
- **Apache PDFBox** - PDF text extraction
- **Lombok** - Reduce boilerplate code

## Future Enhancements

- [ ] Multi-language support
- [ ] Resume formatting suggestions
- [ ] Skill-based recommendations
- [ ] Job market insights
- [ ] Resume templates
- [ ] Interview prep questions based on job description

## Contributing

Feel free to submit issues and pull requests!

## License

MIT License - feel free to use this project however you like.

## Support

For issues and questions, open a GitHub issue or contact the maintainer.
