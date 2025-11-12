const API_BASE = "http://localhost:8080/challenges";

    let challengeData = null;
    let userAnswers = {};
    let timerInterval = null;

    function isoDateShort(dateString) {
        if (!dateString) return "—";
        try {
            return new Date(dateString).toLocaleDateString('pt-BR');
        } catch { return dateString; }
    }

    function escapeHtml(s) {
        return (s+'').replace(/[&<>"']/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
    }

    function updateProgress() {
        const total = challengeData.questions.length;
        const answered = Object.keys(userAnswers).length;
        document.getElementById('answeredCount').textContent = answered;
        document.getElementById('totalCount').textContent = total;

        const percentage = total > 0 ? (answered / total) * 100 : 0;
        document.getElementById('progressFill').style.width = percentage + '%';

        document.getElementById('submitBtn').disabled = answered < total;
    }

    function renderQuestions() {
        const container = document.getElementById('questionsContainer');
        container.innerHTML = '';

        challengeData.questions.forEach((q, qIndex) => {
            const card = document.createElement('div');
            card.className = 'question-card';

            const alternatives = q.alternatives || [];
            const letters = ['A', 'B', 'C', 'D', 'E'];

            card.innerHTML = `
                <span class="question-number">Questão ${qIndex + 1}</span>
                <div class="question-text">${escapeHtml(q.enunciado || q.text || '')}</div>
                <div class="alternatives-list">
                    ${alternatives.map((alt, altIndex) => `
                        <label class="alternative-option">
                            <input type="radio" name="question_${qIndex}" value="${letters[altIndex]}" data-question="${qIndex}">
                            <span class="alternative-letter">${letters[altIndex]}</span>
                            <span class="alternative-text">${escapeHtml(alt.text || '')}</span>
                        </label>
                    `).join('')}
                </div>
            `;

            container.appendChild(card);
        });

        document.querySelectorAll('input[type="radio"]').forEach(radio => {
            radio.addEventListener('change', (e) => {
                const questionIndex = parseInt(e.target.dataset.question);
                userAnswers[questionIndex] = e.target.value;
                updateProgress();
            });
        });
    }

    function startTimer(endDate) {
        const endTime = new Date(endDate).getTime();
        const timerBadge = document.getElementById('timerBadge');
        const timerDisplay = document.getElementById('timerDisplay');

        timerInterval = setInterval(() => {
            const now = new Date().getTime();
            const distance = endTime - now;

            if (distance < 0) {
                clearInterval(timerInterval);
                timerDisplay.textContent = 'Encerrado';
                timerBadge.classList.add('warning');
                document.getElementById('submitBtn').disabled = true;
                document.getElementById('submitBtn').textContent = 'Desafio Encerrado';
                return;
            }

            const days = Math.floor(distance / (1000 * 60 * 60 * 24));
            const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));

            if (days > 0) {
                timerDisplay.textContent = `${days}d ${hours}h`;
            } else if (hours > 0) {
                timerDisplay.textContent = `${hours}h ${minutes}m`;
            } else {
                timerDisplay.textContent = `${minutes}m`;
                if (minutes < 30) {
                    timerBadge.classList.add('warning');
                }
            }
        }, 1000);
    }

    async function loadChallenge() {
        const params = new URLSearchParams(location.search);
        const id = params.get('id');

        if (!id) {
            alert('ID do desafio não fornecido');
            window.location.href = 'challenge.html';
            return;
        }

        try {
            const res = await fetch(`${API_BASE}/${id}`);
            if (!res.ok) throw new Error('Desafio não encontrado');

            challengeData = await res.json();

            if (challengeData.endDate && new Date(challengeData.endDate) < new Date()) {
                alert('Este desafio já foi encerrado.');
                window.location.href = 'challenge.html';
                return;
            }

            document.getElementById('challengeTitle').textContent = challengeData.title;
            document.getElementById('challengeDescription').textContent = challengeData.description;
            document.getElementById('maxScoreDisplay').textContent = challengeData.maxScore || 0;
            document.getElementById('questionCountDisplay').textContent = challengeData.questions?.length || 0;
            document.getElementById('endDateDisplay').textContent = isoDateShort(challengeData.endDate);

            if (challengeData.endDate) {
                document.getElementById('timerBadge').style.display = 'block';
                startTimer(challengeData.endDate);
            }

            renderQuestions();
            updateProgress();

            document.getElementById('loadingScreen').style.display = 'none';
            document.getElementById('challengeContent').style.display = 'block';

        } catch (err) {
            console.error(err);
            alert('Erro ao carregar desafio: ' + err.message);
            window.location.href = 'challenge.html';
        }
    }

    async function submitAnswers() {
        if (Object.keys(userAnswers).length < challengeData.questions.length) {
            alert('Por favor, responda todas as questões antes de enviar.');
            return;
        }

        let correctCount = 0;
        challengeData.questions.forEach((q, index) => {
            const userAnswer = userAnswers[index];
            const correctAlt = q.alternatives.find(a => a.correct);
            const correctLetter = ['A','B','C','D','E'][q.alternatives.indexOf(correctAlt)];

            if (userAnswer === correctLetter) {
                correctCount++;
            }
        });

        const totalQuestions = challengeData.questions.length;
        const scorePercentage = (correctCount / totalQuestions) * 100;
        const finalScore = Math.round((scorePercentage / 100) * challengeData.maxScore);

        document.getElementById('finalScore').textContent = finalScore;
        document.getElementById('finalMaxScore').textContent = challengeData.maxScore;
        document.getElementById('resultMessage').textContent =`Você acertou ${correctCount} de ${totalQuestions} questões (${scorePercentage.toFixed(0)}%)`;

        const resultIcon = document.getElementById('resultIcon');
        if (scorePercentage >= 80) {
            resultIcon.textContent = '🏆';
        } else if (scorePercentage >= 60) {
            resultIcon.textContent = '🎉';
        } else if (scorePercentage >= 40) {
            resultIcon.textContent = '👍';
        } else {
            resultIcon.textContent = '📚';
        }

        document.getElementById('resultModal').classList.add('show');

        // Here you would normally send the results to the backend
        // await fetch(`${API_BASE}/submit/${challengeData.id}`, {
        //     method: 'POST',
        //     headers: { 'Content-Type': 'application/json' },
        //     body: JSON.stringify({ answers: userAnswers, score: finalScore })
        // });
    }

    document.getElementById('submitBtn').addEventListener('click', submitAnswers);

    document.addEventListener('DOMContentLoaded', loadChallenge);

    window.addEventListener('beforeunload', () => {
        if (timerInterval) clearInterval(timerInterval);
    });