document.addEventListener('DOMContentLoaded', () => {
    if (typeof getUserId !== 'function' || typeof getUserType !== 'function') {
        console.error("ERRO: As funções getUserId() e getUserType() não foram encontradas. Inclua seu script de autenticação ANTES deste script no HTML.");
        window.getUserId = () => null;
        window.getUserType = () => null;
    }

    const type = getUserType(); 

    document.querySelectorAll("[data-show]").forEach(el => {
        const allowed = el.dataset.show.split(","); 

        if (!allowed.includes(type) && !allowed.includes("ALL")) {
            el.style.display = "none";
        }
    });

    updateNavVisibility();
    carregarDetalhesCurso();

    const commentForm = document.getElementById('comment-form');
    if (commentForm) {
        commentForm.addEventListener('submit', handleCommentSubmit);
    }
});


function updateNavVisibility() {
    const userId = getUserId();
    document.querySelectorAll("[data-auth]").forEach(el => {
        el.style.display = userId ? 'block' : 'none';
    });
}

async function carregarDetalhesCurso() {
    const urlParams = new URLSearchParams(window.location.search);
    const courseId = urlParams.get('id');

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
        submitButton: document.getElementById('submit-comment-btn'),
        coursePriceModal: document.getElementById('course-price-modal')
    };

    if (!courseId) {
        elements.title.textContent = "Erro: ID do curso não encontrado.";
        return;
    }

    elements.title.textContent = "Carregando...";

    try {
        const loggedUserId = getUserId();
        const loggedUserType = getUserType();

        const response = await fetch(`http://localhost:8080/courses/${courseId}?userId=${loggedUserId}`);

        if (!response.ok) {
            elements.title.textContent = "Curso Não Encontrado";
            return;
        }

        let curso = await response.json();

        let comments = [];
        try {
            const commentsResponse = await fetch(`http://localhost:8080/comments/course/${courseId}`);
            if (commentsResponse.ok) {
                comments = await commentsResponse.json();
            } else {
                console.warn("Falha ao carregar comentários do endpoint dedicado.");
            }
        } catch (commentError) {
            console.error("Erro de rede ao buscar comentários:", commentError);
        }

        elements.title.textContent = curso.title;
        elements.titlePage.textContent = `${curso.title} - Detalhes`;
        elements.subtitle.textContent = curso.description;
        elements.image.src = curso.imageUrl || 'placeholder.jpg';
        elements.image.alt = `Imagem do curso ${curso.title}`;
        elements.description.textContent = curso.description;

        const averageRating = curso.averageRating || 0.0;
        const totalWorkload = curso.workload || 0;
        const teacherName = curso.teacher && curso.teacher.name ? curso.teacher.name : "Professor Desconhecido";
        const teacherId = (curso.teacher && curso.teacher.id) ? String(curso.teacher.id) : '';

        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.id = 'course-teacher-id';
        hiddenInput.value = teacherId;
        document.body.appendChild(hiddenInput);

        elements.videosList.innerHTML = '';
        const isStudentBlocked = loggedUserType === 'ESTUDANTE' && !curso.isPurchased;

        const videoListContent = curso.videos && curso.videos.length > 0
            ? curso.videos.map(video => {
                const buttonStyle = isStudentBlocked
                    ? `opacity: 0.6; cursor: not-allowed;`
                    : `background-color: var(--color-primary);`;

                return `<li class="video-item">
                            <span>${video.title}</span>
                            <button class="btn-play" 
                                data-video-id="${video.id}"
                                data-video-url="${video.videoUrl}"
                                style="margin-left:auto; border:none; padding:6px 12px; border-radius:6px; color:white; cursor:pointer; ${buttonStyle}">
                                ▶ Assistir
                            </button>
                        </li>`;
            }).join('')
            : '<li>Nenhum vídeo listado para este curso.</li>';

        elements.videosList.innerHTML = videoListContent;
        aplicarEventosPlay(courseId, curso.isPurchased);

        elements.sidebar.innerHTML = '';
        const formattedPrice = curso.price.toFixed(2).replace('.', ',');

        const metadataHtml = `
            <div class="course-metadata" style="margin-top: 1.5rem;">
                <p><strong>Preço:</strong> <span style="font-size: 1.5rem; color: var(--color-success); font-weight: bold;">R$ ${formattedPrice}</span></p>
                <p><strong>Professor:</strong> ${teacherName}</p>
                <p><strong>Avaliação:</strong> <span class="rating-stars" style="color: gold;">${'★'.repeat(Math.floor(averageRating))}</span> ${averageRating.toFixed(1)}/5.0</p>
                <p><strong>Carga Horária:</strong> ${totalWorkload}h</p>
            </div>
        `;
        elements.sidebar.insertAdjacentHTML('beforeend', metadataHtml);

        let buttonHtml = '';

        if (loggedUserType === 'ESTUDANTE' && curso.isPurchased) {
            buttonHtml = `
                <button class="buy-button" disabled style="background-color: var(--color-success); cursor: default;">
                   ✅ Curso Adquirido
                </button>
            `;
        } else if (loggedUserType === 'ESTUDANTE' && !curso.isPurchased) {
            buttonHtml = `
                <button id="buy-course-btn" class="buy-button" style="background-color: var(--color-success);">
                   🛒 Comprar Agora
                </button>
            `;
        } else if (!loggedUserId) {
            buttonHtml = `
                <button onclick="window.location.href='login.html'" class="buy-button" style="background-color: var(--color-primary);">
                   Login para Comprar
                </button>
            `;
        }

        elements.sidebar.insertAdjacentHTML('beforeend', buttonHtml);

        const buyButton = document.getElementById('buy-course-btn');
        if (buyButton) {
            if(elements.coursePriceModal) elements.coursePriceModal.textContent = formattedPrice;

            buyButton.addEventListener('click', () => {
                document.getElementById('payment-modal').style.display = 'block';
            });

            const paymentForm = document.getElementById('payment-form');
            if (paymentForm) {
                paymentForm.addEventListener('submit', (e) => handlePaymentSubmit(e, courseId, curso.price));
            }
        }

        if (elements.commentsContainer) {
            renderizarComentarios(comments || []);
        }

        let formMessage = '';
        let showForm = true;

        let existingComment = null;
        if (comments && loggedUserId) {
            existingComment = comments.find(c => c.author && String(c.author.id) === String(loggedUserId));
        }

        if (elements.commentForm) {
            elements.commentForm.reset();
            if(elements.commentContent) elements.commentContent.value = '';
            elements.commentForm.setAttribute('data-action', 'create');
        }

        if (!loggedUserId) {
            formMessage = '<p class="error-message" style="color: var(--color-primary); margin-top: 0;">Faça login para deixar uma avaliação.</p>';
            showForm = false;
        } else if (loggedUserType === 'PROFESSOR') {
            formMessage = '<p class="error-message">Como professor, você não pode avaliar este curso.</p>';
            showForm = false;
        }
        else if (existingComment) {
            formMessage = '<p style="color: var(--color-success);">Você já avaliou este curso. Modifique sua avaliação abaixo.</p>';
            showForm = true;

            if (elements.commentForm) {
                const ratingValue = Math.round(existingComment.rating);
                const starInput = document.getElementById(`star${ratingValue}`);
                if (starInput) {
                    starInput.checked = true;
                }

                if(elements.commentContent) elements.commentContent.value = existingComment.content;

                if(elements.submitButton) elements.submitButton.textContent = 'Atualizar Avaliação';
                elements.commentForm.setAttribute('data-action', 'update');
            }
        } else {
            if (elements.commentForm && elements.submitButton) {
                elements.submitButton.textContent = 'Enviar Avaliação';
                elements.commentForm.setAttribute('data-action', 'create');
            }
        }

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

async function handlePaymentSubmit(e, courseId, price) {
    e.preventDefault();

    const cardNumber = document.getElementById('card-number').value.trim();
    const cardName = document.getElementById('card-name').value.trim();
    const cardExpiry = document.getElementById('card-expiry').value.trim();
    const cardCvv = document.getElementById('card-cvv').value.trim();

    const studentIdStr = getUserId();
    const courseIdStr = courseId;

    const studentIdNum = Number(studentIdStr);
    const courseIdNum = Number(courseIdStr);

    if (!studentIdStr || studentIdStr === 'null' || isNaN(studentIdNum)) {
        Swal.fire('Erro', 'ID do estudante inválido. Certifique-se de que está logado corretamente.', 'error');
        return;
    }
    if (!courseIdStr || courseIdStr === 'null' || isNaN(courseIdNum)) {
        Swal.fire('Erro', 'ID do curso inválido.', 'error');
        return;
    }
    console.log(`Tentando matricular Estudante ID: ${studentIdNum} no Curso ID: ${courseIdNum}`);

    if (cardNumber.length < 16 || cardName.length < 3 || cardExpiry.length < 5 || cardCvv.length < 3) {
        Swal.fire('Atenção', 'Preencha todos os dados do cartão corretamente.', 'warning');
        return;
    }

    document.getElementById('payment-modal').style.display = 'none';
    Swal.fire({
        title: 'Processando Pagamento...',
        text: 'Aguarde a confirmação da transação.',
        icon: 'info',
        allowOutsideClick: false,
        showConfirmButton: false,
        willOpen: () => {
            Swal.showLoading();
        }
    });

    await new Promise(resolve => setTimeout(resolve, 2000));

    try {
        const buyResponse = await fetch(`http://localhost:8080/Students/${studentIdNum}/courses/${courseIdNum}`, {

            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                pricePaid: price
            })
        });

        if (buyResponse.ok) {
            Swal.fire({
                icon: 'success',
                title: 'Compra Realizada!',
                text: `O curso ${courseIdNum} foi adicionado ao seu perfil.`,
                timer: 3000
            }).then(() => {
                window.location.reload();
            });
        } else {
            const errorData = await buyResponse.json().catch(() => ({ message: 'Erro desconhecido ou servidor não retornou JSON.' }));
            console.error("Erro do Backend:", buyResponse.status, errorData);

            let errorMessage = errorData.message || 'Ocorreu um erro ao processar a compra.';

            if (buyResponse.status === 404) {
                errorMessage = 'Erro 404: Aluno ou Curso não encontrado no servidor. Verifique os IDs.';
            } else if (buyResponse.status === 409) {
                errorMessage = 'Você já está matriculado neste curso.';
            } else if (buyResponse.status >= 500) {
                errorMessage = 'Erro interno do servidor ao processar a matrícula.';
            }

            Swal.fire({
                icon: 'error',
                title: 'Falha na Transação!',
                text: errorMessage,
                timer: 5000
            });
        }
    } catch (error) {
        console.error("Erro ao registrar a compra:", error);
        Swal.fire('Erro de Rede', 'Não foi possível conectar ao servidor para finalizar a compra.', 'error');
    }
}

function renderizarComentarios(comments) {
    const container = document.getElementById('comments-container');
    container.innerHTML = '';

    const loggedUserId = getUserId();
    const loggedUserType = getUserType();
    const courseTeacherId = String(document.getElementById('course-teacher-id').value);

    if (comments.length === 0) {
        container.innerHTML = '<p>Este curso ainda não possui avaliações.</p>';
        return;
    }

    comments.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    comments.forEach(comment => {
        const formattedDate = new Date(comment.createdAt).toLocaleDateString('pt-BR', { year: 'numeric', month: 'short', day: 'numeric' });
        const authorName = comment.authorName || (comment.author && comment.author.name ? comment.author.name : 'Usuário Desconhecido');

        const rating = comment.rating || 0;
        const filledStars = '★'.repeat(Math.floor(rating));
        const emptyStars = '☆'.repeat(5 - Math.floor(rating));
        const isTeacherOfCourse = loggedUserType === 'PROFESSOR' && String(loggedUserId) === courseTeacherId;
        const needsReplyForm = isTeacherOfCourse && !comment.response;

        const commentHtml = `
            <div class="comment-item" data-comment-id="${comment.id}">
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

                ${needsReplyForm ? `
                    <form class="reply-form" data-comment-id="${comment.id}" style="margin-top: 15px; padding-top: 10px; border-top: 1px solid #eee;">
                        <textarea class="form-textarea reply-content" placeholder="Responda o aluno..." required style="margin-bottom: 5px;"></textarea>
                        <button type="submit" class="btn btn-sm btn-primary">Responder</button>
                    </form>
                ` : ''}
            </div>
        `;
        container.innerHTML += commentHtml;
    });

    async function handleReplySubmit(e) {
        e.preventDefault();

        const form = e.target;
        const commentId = form.getAttribute('data-comment-id');
        const replyContent = form.querySelector('.reply-content').value.trim();
        const professorId = getUserId();

        if (!replyContent || replyContent.length < 5) {
            Swal.fire('Erro', 'A resposta deve ter pelo menos 5 caracteres.', 'warning');
            return;
        }

        const payload = {
            professorId: Number(professorId),
            reply: replyContent
        };

        try {
            const response = await fetch(`http://localhost:8080/comments/${commentId}/reply`, {
                method: "PUT",
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (response.ok) {
                Swal.fire({
                    icon: 'success',
                    title: 'Resposta Enviada!',
                    text: 'A resposta foi registrada com sucesso.',
                    timer: 2000,
                    showConfirmButton: false,
                    position: "top"
                }).then(() => {
                    carregarDetalhesCurso();
                });
            } else {
                const errorData = await response.json();
                Swal.fire('Falha ao Responder', errorData.message || 'Ocorreu um erro ao enviar a resposta.', 'error');
            }

        } catch (error) {
            console.error("Erro ao enviar resposta:", error);
            Swal.fire('Erro de Rede', 'Não foi possível conectar ao servidor para responder.', 'error');
        }
    }

    document.querySelectorAll('.reply-form').forEach(form => {
        form.addEventListener('submit', handleReplySubmit);
    });
}

async function handleCommentSubmit(e) {
    e.preventDefault();

    const form = e.target;
    const courseId = String(form.getAttribute('data-course-id'));
    const actionType = form.getAttribute('data-action') || 'create';
    const userId = String(getUserId());
    const ratingElement = document.querySelector('input[name="rating"]:checked');
    const content = document.getElementById('comment-content').value.trim();

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

    const commentData = {
        userId: Number(userId),
        courseId: Number(courseId),
        rating: Number(ratingElement.value),
        content: content
    };

    const method = actionType === 'update' ? 'PUT' : 'POST';
    const url = `http://localhost:8080/comments`;

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(commentData)
        });

        if (response.status === 403 || response.status === 400) {
            const errorData = await response.json();
            Swal.fire('Ação Não Permitida', errorData.message || 'Erro de regra de negócio.', 'error');
            carregarDetalhesCurso();
            return;
        }

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
            carregarDetalhesCurso();
        });

    } catch (error) {
        console.error("Erro ao enviar comentário:", error);
        Swal.fire('Erro de Rede', 'Não foi possível conectar ao servidor.', 'error');
    }
}

function aplicarEventosPlay(courseId, isPurchased) {
    const buttons = document.querySelectorAll(".btn-play");
    const studentId = getUserId();
    const userType = getUserType();

    buttons.forEach(btn => {
        btn.addEventListener("click", async () => {

            const videoId = btn.getAttribute("data-video-id");
            const videoUrl = btn.getAttribute("data-video-url");

            if (!studentId) {
                Swal.fire("Atenção!", "Faça login para assistir.", "warning");
                return;
            }

            if (userType === 'ESTUDANTE' && !isPurchased) {
                Swal.fire({
                    title: "Acesso Restrito",
                    text: "Você precisa comprar o curso para assistir aos vídeos. Adquira-o abaixo!",
                    icon: "warning",
                    showConfirmButton: true,
                    confirmButtonText: "Entendi"
                });
                return;
            }

            if (!videoUrl) {
                Swal.fire("Erro", "URL do vídeo não encontrada.", "error");
                return;
            }

            if (userType === 'ESTUDANTE') {
                await marcarProgressoVideo(studentId, courseId, videoId);
            }

            window.open(videoUrl, '_blank');
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