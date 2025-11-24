document.addEventListener('DOMContentLoaded', () => {
    // 1. Inicialização e Verificação de Autenticação
    if (typeof getUserId !== 'function' || typeof getUserType !== 'function') {
        console.error("ERRO: As funções getUserId() e getUserType() não foram encontradas. Inclua seu script de autenticação ANTES deste script no HTML.");
        // Mock functions para evitar crash
        window.getUserId = () => null;
        window.getUserType = () => null;
    }

    updateNavVisibility();
    carregarDetalhesCurso();

    const commentForm = document.getElementById('comment-form');
    if (commentForm) {
        commentForm.addEventListener('submit', handleCommentSubmit);
    }
});

/**
 * Função utilitária para esconder/mostrar elementos de navegação.
 */
function updateNavVisibility() {
    // Implementação da navegação aqui (omitida para foco)
    const userId = getUserId();
    document.querySelectorAll("[data-auth]").forEach(el => {
        // Exemplo: mostrar links se userId existir
        el.style.display = userId ? 'block' : 'none';
    });
}

/**
 * Carrega todos os dados do curso do backend e preenche a página.
 */
async function carregarDetalhesCurso() {
    const urlParams = new URLSearchParams(window.location.search);
    const courseId = urlParams.get('id');

    // Mapeamento dos elementos do DOM
    const elements = {
        title: document.getElementById('detail-title'),
        titlePage: document.getElementById('course-title-page'),
        subtitle: document.getElementById('detail-subtitle'),
        image: document.getElementById('detail-image'),
        description: document.getElementById('detail-description'),
        videosList: document.getElementById('videos-list'),
        sidebar: document.querySelector('.course-sidebar'),
        commentsContainer: document.getElementById('comments-container'),
        commentForm: document.getElementById('comment-form'),
        commentFormMessage: document.getElementById('comment-form-message'),
        commentContent: document.getElementById('comment-content'),
        submitButton: document.getElementById('submit-comment-btn')
    };

    // Validação inicial do ID
    if (!courseId) {
        elements.title.textContent = "Erro: ID do curso não encontrado.";
        return;
    }

    elements.title.textContent = "Carregando...";

    try {
        const response = await fetch(`http://localhost:8080/courses/${courseId}`);

        if (!response.ok) {
            elements.title.textContent = "Curso Não Encontrado";
            return;
        }

        const curso = await response.json();

        // --- 2. Popular os Detalhes Principais ---
        elements.title.textContent = curso.title;
        elements.titlePage.textContent = `${curso.title} - Detalhes`;
        elements.subtitle.textContent = curso.description;
        elements.image.src = curso.imageUrl || 'placeholder.jpg';
        elements.image.alt = `Imagem do curso ${curso.title}`;
        elements.description.textContent = curso.description;

        const averageRating = curso.averageRating || 0.0;
        const totalWorkload = curso.workload || 0;
        const teacherName = curso.teacher && curso.teacher.name ? curso.teacher.name : "Professor Desconhecido";

        // --- 3. Renderizar Vídeos (Omitido por brevidade, mas deve existir) ---
        // --- 3. Renderizar Vídeos ---
        elements.videosList.innerHTML = '';

        if (!curso.videos || curso.videos.length === 0) {
            elements.videosList.innerHTML = '<li>Nenhum vídeo listado para este curso.</li>';
        } else {
            curso.videos.forEach(video => {

                const item = document.createElement('li');
                item.classList.add('video-item');

                item.innerHTML = `
                    <span>${video.title}</span>
                    <button class="btn-play" data-video-id="${video.id}" style="
                        margin-left:auto;
                        background-color: var(--color-primary);
                        border:none; padding:6px 12px;
                        border-radius:6px; color:white; cursor:pointer;">
                        ▶ Assistir
                    </button>
                `;

                elements.videosList.appendChild(item);
            });

            aplicarEventosPlay(courseId);
        }

        // ... Lógica para preencher elements.videosList ...

        // --- 4. Lógica da Sidebar (Incluindo Botão de Gestão) ---
        const loggedUserId = getUserId();
        const loggedUserType = getUserType();
        const teacherId = (curso.teacher && curso.teacher.id) ? String(curso.teacher.id) : '';

        elements.sidebar.innerHTML = ''; // Limpa a sidebar

        // BOTÃO DE GESTÃO: Visível apenas para o professor DO CURSO
        if (loggedUserType === 'PROFESSOR' && String(loggedUserId) === teacherId) {
            const managementButtonHtml = `
                <a href="course-teacher-management.html?courseId=${courseId}" 
                   class="btn btn-primary" 
                   style="display: block; text-align: center; margin-bottom: 15px; background-color: var(--color-primary); color: white; padding: 10px; border-radius: 4px; text-decoration: none;">
                   👩‍🏫 Gerenciar Feedbacks
                </a>
            `;
            elements.sidebar.insertAdjacentHTML('beforeend', managementButtonHtml);
        }

        // Adiciona Metadados à Sidebar
        const metadataHtml = `
            <div class="course-metadata" style="margin-top: 1.5rem;">
                <p><strong>Professor:</strong> ${teacherName}</p>
                <p><strong>Avaliação:</strong> <span class="rating-stars" style="color: gold;">${'★'.repeat(Math.floor(averageRating))}</span> ${averageRating.toFixed(1)}/5.0</p>
                <p><strong>Carga Horária:</strong> ${totalWorkload}h</p>
                </div>
        `;
        elements.sidebar.insertAdjacentHTML('beforeend', metadataHtml);


        // --- 5. Renderizar Comentários ---
        if (elements.commentsContainer) {
            renderizarComentarios(curso.comments || []);
        }

        // --- 6. Configurar o Formulário de Comentário (Criar ou Modificar) ---
        let formMessage = '';
        let showForm = true;
        let existingComment = null;

        if (curso.comments) {
            existingComment = curso.comments.find(c => String(c.author.id) === String(loggedUserId));
        }

        // Limpa o formulário antes de re-preencher
        if (elements.commentForm) {
            elements.commentForm.reset();
            // Limpa o conteúdo (caso o form.reset não remova o valor de textarea)
            if(elements.commentContent) elements.commentContent.value = '';
            // Limpa a ação padrão
            elements.commentForm.setAttribute('data-action', 'create');
        }

        if (!loggedUserId) {
            formMessage = '<p class="error-message" style="color: var(--color-primary); margin-top: 0;">Faça login para deixar uma avaliação.</p>';
            showForm = false;
        } else if (loggedUserType === 'PROFESSOR') {
            formMessage = '<p class="error-message">Como professor, você não pode avaliar este curso.</p>';
            showForm = false;
        }
        // LÓGICA CHAVE: Se o comentário EXISTE, permite MODIFICAÇÃO
        else if (existingComment) {
            formMessage = '<p style="color: var(--color-success);">Você já avaliou este curso. Modifique sua avaliação abaixo.</p>';
            showForm = true;

            if (elements.commentForm) {
                // Preenche o rating (arredonda para o inteiro mais próximo para preencher o rádio)
                const ratingValue = Math.round(existingComment.rating);
                const starInput = document.getElementById(`star${ratingValue}`);
                if (starInput) {
                    starInput.checked = true;
                }

                if(elements.commentContent) elements.commentContent.value = existingComment.content;

                // Configura a ação para UPDATE (será enviado via PUT)
                if(elements.submitButton) elements.submitButton.textContent = 'Atualizar Avaliação';
                elements.commentForm.setAttribute('data-action', 'update');
            }
        } else {
            // NOVO COMENTÁRIO (Criação - será enviado via POST)
            if (elements.commentForm && elements.submitButton) {
                elements.submitButton.textContent = 'Enviar Avaliação';
                elements.commentForm.setAttribute('data-action', 'create');
            }
        }

        // 6.3 Lógica final de exibição do formulário
        if (elements.commentForm) {
            if (showForm) {
                elements.commentForm.setAttribute('data-course-id', courseId);
                elements.commentForm.style.display = 'block';
                if(elements.commentFormMessage) elements.commentFormMessage.innerHTML = formMessage;
            } else {
                elements.commentForm.style.display = 'none';
                if(elements.commentFormMessage) elements.commentFormMessage.innerHTML = formMessage;
            }
        }

    } catch (error) {
        console.error("Erro fatal ao carregar detalhes do curso:", error);
        elements.title.textContent = "Erro ao Carregar Detalhes";
        elements.description.textContent = "Não foi possível carregar os dados. Verifique a conexão.";
    }
}

/**
 * Renderiza a lista de comentários.
 */
function renderizarComentarios(comments) {
    const container = document.getElementById('comments-container');
    container.innerHTML = '';

    if (comments.length === 0) {
        container.innerHTML = '<p>Este curso ainda não possui avaliações.</p>';
        return;
    }

    comments.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    comments.forEach(comment => {
        const formattedDate = new Date(comment.createdAt).toLocaleDateString('pt-BR', { year: 'numeric', month: 'short', day: 'numeric' });
        // O `authorName` vem do backend, garantindo o nome do autor mesmo se o objeto `author` for parcial
        const authorName = comment.authorName || (comment.author && comment.author.name ? comment.author.name : 'Usuário Desconhecido');

        const rating = comment.rating || 0;
        const filledStars = '★'.repeat(Math.floor(rating));
        const emptyStars = '☆'.repeat(5 - Math.floor(rating));

        const commentHtml = `
            <div class="comment-item">
                <div class="comment-header">
                    <strong>${authorName}</strong>
                    <span>${formattedDate}</span>
                </div>
                <div class="comment-rating" style="color: gold;">
                    ${filledStars}${emptyStars} (${rating.toFixed(1)})
                </div>
                <p>${comment.content}</p>
                
                ${comment.response ? `
                    <div class="teacher-response" style="background-color: #f0f8ff; border-left: 3px solid #007bff; padding: 10px; margin-top: 10px;">
                        <strong>Resposta do Professor:</strong>
                        <p style="margin: 0;">${comment.response}</p>
                    </div>
                ` : ''}
            </div>
        `;
        container.innerHTML += commentHtml;
    });
}

/**
 * Envia ou Atualiza o comentário e a avaliação para o backend (POST ou PUT).
 */
async function handleCommentSubmit(e) {
    e.preventDefault();

    const form = e.target;
    const courseId = String(form.getAttribute('data-course-id'));
    // Obtém a ação definida na função carregarDetalhesCurso
    const actionType = form.getAttribute('data-action') || 'create';
    const userId = String(getUserId());
    const ratingElement = document.querySelector('input[name="rating"]:checked');
    const content = document.getElementById('comment-content').value.trim();

    // 1. Validações
    if (!userId || !courseId || userId === 'null' || courseId === 'null') {
        Swal.fire('Erro', 'Você precisa estar logado para comentar.', 'warning');
        return;
    }
    if (!ratingElement) {
        Swal.fire('Erro', 'Por favor, selecione uma avaliação em estrelas.', 'warning');
        return;
    }
    if (content.length < 5) {
        Swal.fire('Erro', 'O comentário deve ter pelo menos 5 caracteres.', 'warning');
        return;
    }

    // 2. Payload (Garantindo que os IDs e Rating são Numbers)
    const commentData = {
        userId: Number(userId),
        courseId: Number(courseId),
        rating: Number(ratingElement.value),
        content: content
    };

    // 3. Determina o Método HTTP e URL
    const method = actionType === 'update' ? 'PUT' : 'POST';
    const url = `http://localhost:8080/comments`;

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(commentData)
        });

        // 4. Tratamento de Erro de Regra de Negócio (403 Forbidden)
        if (response.status === 403 || response.status === 400) {
            const errorData = await response.json();
            Swal.fire('Ação Não Permitida', errorData.message || 'Erro de regra de negócio.', 'error');
            carregarDetalhesCurso(); // Recarrega para refletir o estado correto do formulário
            return;
        }

        // 5. Tratamento de Sucesso
        if (!response.ok) {
            const errorData = await response.json();
            Swal.fire('Falha ao Enviar', errorData.message || 'Ocorreu um erro ao enviar sua avaliação.', 'error');
            return;
        }

        const successMessage = actionType === 'update' ? 'Sua avaliação foi atualizada com sucesso.' : 'Sua avaliação foi enviada com sucesso.';

        Swal.fire({
            icon: 'success',
            title: 'Obrigado!',
            text: successMessage,
            timer: 2000,
            showConfirmButton: false,
            position: "top"
        }).then(() => {
            carregarDetalhesCurso(); // Recarrega para atualizar a lista de comentários e o formulário
        });

    } catch (error) {
        console.error("Erro ao enviar comentário:", error);
        Swal.fire('Erro de Rede', 'Não foi possível conectar ao servidor.', 'error');
    }
}

aplicarEventosPlay(courseId);

function aplicarEventosPlay(courseId) {
    const buttons = document.querySelectorAll(".btn-play");

    buttons.forEach(btn => {
        btn.addEventListener("click", async () => {

            const videoId = btn.getAttribute("data-video-id");
            const studentId = getUserId(); // estudante logado

            if (!studentId) {
                Swal.fire("Atenção!", "Faça login para assistir.", "warning");
                return;
            }

            await marcarProgressoVideo(studentId, courseId, videoId);

            window.location.href = `video-player.html?courseId=${courseId}&videoId=${videoId}`;
        });
    });
}

async function marcarProgressoVideo(studentId, courseId, videoId) {
    try {
        const response = await fetch("http://localhost:8080/progress/complete-video", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                studentId: Number(studentId),
                courseId: Number(courseId),
                videoId: Number(videoId)
            })
        });

        if (!response.ok) {
            const err = await response.json().catch(() => null);
            console.warn("Erro ao registrar progresso:", err?.message || response.status);
            return;
        }

        console.log("Progresso registrado para o vídeo:", videoId);

    } catch (error) {
        console.error("Erro ao marcar progresso:", error);
    }
}