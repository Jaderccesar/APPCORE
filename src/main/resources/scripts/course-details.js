document.addEventListener('DOMContentLoaded', () => {
    if (typeof getUserId !== 'function' || typeof getUserType !== 'function') {
        console.error("Erro: As funções getUserId() e getUserType() não foram encontradas. Verifique se o arquivo de autenticação está linkado antes deste script.");
        return;
    }
    carregarDetalhesCurso();
});

async function carregarDetalhesCurso() {
    const urlParams = new URLSearchParams(window.location.search);
    const courseId = urlParams.get('id');

    const elements = {
        title: document.getElementById('detail-title'),
        titlePage: document.getElementById('course-title-page'),
        subtitle: document.getElementById('detail-subtitle'),
        image: document.getElementById('detail-image'),
        description: document.getElementById('detail-description'),
        price: document.getElementById('detail-price-sidebar'), // Será preenchido dinamicamente
        level: document.getElementById('detail-level'),
        workload: document.getElementById('detail-workload'),
        certificate: document.getElementById('detail-certificate'),
        rating: document.getElementById('detail-rating'),
        teacher: document.getElementById('detail-teacher'),
        videosList: document.getElementById('videos-list'),
        sidebar: document.querySelector('.course-sidebar')
    };

    if (!courseId) {
        elements.title.textContent = "Erro: ID do curso não encontrado.";
        return;
    }

    elements.title.textContent = "Carregando...";
    elements.description.textContent = "Aguarde enquanto carregamos os detalhes do curso.";

    try {
        const response = await fetch(`http://localhost:8080/courses/${courseId}`);

        if (!response.ok) {
            elements.title.textContent = "Curso Não Encontrado";
            elements.subtitle.textContent = `Nenhum curso com o ID ${courseId} foi encontrado.`;
            return;
        }

        const curso = await response.json();

        elements.title.textContent = curso.title;
        elements.titlePage.textContent = `${curso.title} - Detalhes`;
        elements.subtitle.textContent = curso.description;
        elements.image.src = curso.imageUrl || 'caminho/para/imagem/placeholder.jpg';
        elements.image.alt = `Imagem do curso ${curso.title}`;
        elements.description.textContent = curso.description;

        const priceText = curso.price > 0 ? "R$ " + curso.price.toFixed(2) : "Gratuito";

        elements.level.textContent = curso.level;
        elements.workload.textContent = curso.workload;
        elements.certificate.textContent = curso.certificateEnabled ? "Sim" : "Não";
        elements.rating.textContent = curso.rating ? `${curso.rating.toFixed(1)}/5.0` : "0.0/5.0";
        elements.teacher.textContent = curso.teacher && curso.teacher.name ? curso.teacher.name : "Professor Desconhecido";


        if (curso.videos && curso.videos.length > 0) {
            elements.videosList.innerHTML = curso.videos
                .sort((a, b) => (a.orderNumber || 0) - (b.orderNumber || 0))
                .map(video => `
                    <li>${video.orderNumber ? video.orderNumber + '. ' : ''}${video.title} (${video.duration || 'Duração não informada'})</li>
                `).join('');
        } else {
            elements.videosList.innerHTML = '<li>Nenhum vídeo listado para este curso.</li>';
        }

        const loggedUserId = getUserId();
        const loggedUserType = getUserType();
        const teacherId = curso.teacher && curso.teacher.id ? String(curso.teacher.id) : null;

        elements.sidebar.innerHTML = '';


        if (loggedUserType === 'TEACHER' && loggedUserId === teacherId) {
            const editButton = document.createElement('a');
            editButton.href = `course-edit.html?id=${courseId}`;
            editButton.className = 'btn btn-primary btn-block buy-button';
            editButton.textContent = 'Editar Curso';
            editButton.style.backgroundColor = 'var(--color-primary)';
            editButton.style.marginTop = '1rem';

            elements.sidebar.innerHTML = `
                <h2 style="margin-top: 0; text-align: center;">Área do Professor</h2>
                <p style="text-align: center;">Você é o criador deste curso.</p>
            `;
            elements.sidebar.appendChild(editButton);

        } else if (loggedUserId) {
            const buyButton = document.createElement('button');
            buyButton.className = 'buy-button';
            buyButton.textContent = 'Comprar Agora';
            buyButton.onclick = () => alert(`Iniciando checkout do curso: ${curso.title}`);

            elements.sidebar.innerHTML = `
                <h2 style="margin-top: 0; text-align: center;">${priceText}</h2>
            `;
            elements.sidebar.appendChild(buyButton);

        } else {
            elements.sidebar.innerHTML = `
                <h2 style="margin-top: 0; text-align: center;">${priceText}</h2>
                <button class="buy-button" style="background-color: var(--color-danger);" onclick="window.location.href='user-register.html'">
                    Faça Login para Comprar
                </button>
            `;
        }

        const metadataHtml = `
            <div class="course-metadata" style="margin-top: 1.5rem;">
                <p><strong>Nível:</strong> ${elements.level.textContent}</p>
                <p><strong>Carga Horária:</strong> ${elements.workload.textContent}h</p>
                <p><strong>Certificado:</strong> ${elements.certificate.textContent}</p>
                <p><strong>Avaliação:</strong> <span class="rating-stars">★★★★★</span> ${elements.rating.textContent}</p>
                <p><strong>Professor:</strong> ${elements.teacher.textContent}</p>
            </div>
        `;
        elements.sidebar.insertAdjacentHTML('beforeend', metadataHtml);


    } catch (error) {
        console.error("Erro fatal ao carregar detalhes do curso:", error);
        elements.title.textContent = "Erro ao Carregar Detalhes";
        elements.description.textContent = "Não foi possível carregar os dados. Verifique a conexão com o servidor ou o formato da resposta do backend.";
    }
}