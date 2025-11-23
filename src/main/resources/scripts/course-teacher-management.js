// Arquivo: ../scripts/course-teacher-management.js (Reforçado)

document.addEventListener('DOMContentLoaded', () => {
    // Funções utilitárias devem estar disponíveis
    if (typeof getUserId !== 'function' || typeof getUserType !== 'function') {
        console.error("ERRO: As funções getUserId() e getUserType() não foram encontradas.");
        return;
    }

    // 1. Inicialização e Busca de IDs
    const courseId = getCourseIdFromUrl();
    const professorId = getUserId();

    if (!courseId || !professorId) {
        console.error("ERRO CRÍTICO: courseId ou professorId ausente.", { courseId, professorId });
        const container = document.getElementById('feedbacks-container');
        if (container) {
            container.innerHTML = '<p class="alert-error">IDs ausentes. Verifique se você está logado e se o ID do curso está correto na URL.</p>';
        }
        return;
    }

    // Carrega o título do curso primeiro
    fetchCourseTitle(courseId);
    // Carrega os feedbacks
    loadCourseFeedbacks(courseId, professorId);
});

/**
 * Obtém o ID do curso da URL (Assume ?courseId=123)
 */
function getCourseIdFromUrl() {
    const urlParams = new URLSearchParams(window.location.search);
    // Retorna o ID como String
    return urlParams.get('courseId');
}

/**
 * Obtém e exibe o título do curso.
 */
async function fetchCourseTitle(courseId) {
    try {
        const response = await fetch(`http://localhost:8080/courses/${Number(courseId)}`);
        if (response.ok) {
            const course = await response.json();
            document.getElementById('course-title-display').textContent = `Curso: ${course.title}`;
            document.title = `Gestão de Feedback - ${course.title}`;
        }
    } catch (error) {
        console.error("Erro ao carregar título do curso:", error);
    }
}


/**
 * 1. Carrega todos os feedbacks de um curso para o professor.
 */
async function loadCourseFeedbacks(courseId, professorId) {
    const container = document.getElementById('feedbacks-container');
    if (!container) return;

    container.innerHTML = '<p class="alert-loading">Carregando Feedbacks...</p>';

    try {
        // Garantindo que IDs são Numbers para a chamada ao endpoint Java
        const response = await fetch(`http://localhost:8080/comments/course/${Number(courseId)}/professor/${Number(professorId)}`);

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ message: response.statusText }));
            container.innerHTML = `<p class="alert-error">Falha no Servidor. Código: ${response.status}. ${errorData.message || 'Erro desconhecido.'}</p>`;
            return;
        }

        const feedbacks = await response.json();
        renderFeedbacks(feedbacks, professorId, container);

    } catch (error) {
        console.error("Erro de rede/JSON ao carregar feedbacks:", error);
        container.innerHTML = '<p class="alert-error">Erro de conexão. Verifique o console para mais detalhes.</p>';
    }
}

/**
 * 2. Renderiza a lista de comentários com o formulário de resposta.
 */
function renderFeedbacks(feedbacks, professorId, container) {
    container.innerHTML = ''; // Limpa o loading message

    if (feedbacks.length === 0) {
        container.innerHTML = '<p class="alert-loading">Nenhum aluno avaliou este curso ainda.</p>';
        return;
    }

    // Adiciona o container da lista para organizar
    const feedbackListDiv = document.createElement('div');
    feedbackListDiv.classList.add('feedback-list');

    feedbacks.forEach(comment => {
        const formattedDate = new Date(comment.createdAt).toLocaleDateString('pt-BR');
        const authorName = comment.authorName || 'Usuário Desconhecido';
        const rating = comment.rating || 0;
        const filledStars = '★'.repeat(Math.floor(rating));
        const emptyStars = '☆'.repeat(5 - Math.floor(rating));

        const commentDiv = document.createElement('div');
        commentDiv.classList.add('comment-item');

        const currentResponse = comment.response || '';

        commentDiv.innerHTML = `
            <div class="comment-header">
                <span class="comment-author">${authorName}</span>
                <span class="comment-date">${formattedDate}</span>
            </div>
            <div class="comment-rating">
                ${filledStars}${emptyStars} (${rating.toFixed(1)})
            </div>
            <p class="comment-content">${comment.content}</p>
            
            <div class="teacher-response-box">
                <strong class="${currentResponse ? 'text-success' : 'text-warning'}">
                    ${currentResponse ? 'Resposta Atual:' : 'Aguardando Resposta'}
                </strong>
                <p class="response-text">${currentResponse || 'Você ainda não respondeu a este feedback.'}</p>
            </div>

            <form class="reply-form" data-comment-id="${comment.id}" data-professor-id="${professorId}">
                <textarea 
                    name="reply" 
                    placeholder="Sua resposta ao aluno (máx. 500 caracteres)" 
                    maxlength="500" 
                    required
                >${currentResponse}</textarea>
                <button type="submit" class="btn btn-submit">
                    ${currentResponse ? 'Atualizar Resposta' : 'Enviar Resposta'}
                </button>
            </form>
        `;

        feedbackListDiv.appendChild(commentDiv);
    });

    container.appendChild(feedbackListDiv);

    // Adiciona event listeners aos formulários após a renderização
    document.querySelectorAll('.reply-form').forEach(form => {
        // Remove listeners antigos para evitar duplicação em recarregamento
        form.removeEventListener('submit', handleReplySubmit);
        form.addEventListener('submit', handleReplySubmit);
    });
}

/**
 * 3. Envia a resposta do professor para o backend.
 */
async function handleReplySubmit(e) {
    e.preventDefault();

    const form = e.target;
    const commentId = form.getAttribute('data-comment-id');
    const professorId = form.getAttribute('data-professor-id');
    const replyContent = form.querySelector('textarea[name="reply"]').value.trim();

    if (!replyContent) {
        Swal.fire('Atenção', 'O campo de resposta não pode estar vazio.', 'warning');
        return;
    }

    const replyData = {
        professorId: Number(professorId),
        reply: replyContent
    };

    try {
        const response = await fetch(`http://localhost:8080/comments/${commentId}/reply`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(replyData)
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ message: 'Resposta inválida do servidor.' }));
            Swal.fire('Falha ao Responder', errorData.message || 'Erro ao enviar a resposta.', 'error');
            return;
        }

        Swal.fire({
            icon: 'success',
            title: 'Resposta Enviada!',
            text: 'A resposta foi registrada.',
            timer: 1500,
            showConfirmButton: false,
            position: "top"
        }).then(() => {
            // Recarrega apenas a lista de feedbacks
            const courseId = getCourseIdFromUrl();
            loadCourseFeedbacks(courseId, professorId);
        });

    } catch (error) {
        console.error("Erro ao enviar resposta:", error);
        Swal.fire('Erro de Rede', 'Não foi possível conectar ao servidor.', 'error');
    }
}