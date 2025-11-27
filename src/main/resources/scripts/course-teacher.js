document.addEventListener('DOMContentLoaded', () => {
    // Garante que as funções de autenticação estão disponíveis.
    if (typeof getUserId !== 'function' || typeof getUserType !== 'function') {
        alert("Erro de Autenticação: getUserId()/getUserType() não definidos. Inclua auth.js.");
        return;
    }

    const userId = getUserId();
    const userType = getUserType();

    // Validação de Acesso
    if (!userId || userType !== 'PROFESSOR') {
        const grid = document.getElementById("teacher-courses-grid");
        grid.innerHTML = '<p class="error-message">Acesso negado. Esta página é exclusiva para Professores logados.</p>';
        return;
    }


    document.querySelectorAll("[data-show]").forEach(el => {
        const allowed = el.dataset.show.split(",");
        if (!allowed.includes(userType) && !allowed.includes("ALL")) {
            el.style.display = "none";
        }
    });

    carregarCursosDoProfessor(userId);
});

async function carregarCursosDoProfessor(teacherId) {
    const grid = document.getElementById("teacher-courses-grid");
    grid.innerHTML = "<p>Carregando seus cursos...</p>";

    // Endpoint corrigido (use localhost ou 127.0.0.1 consistentemente)
    const endpoint = `http://localhost:8080/courses/teacher/${teacherId}`;

    try {
        const response = await fetch(endpoint);

        if (!response.ok) {
            console.error(`Falha na requisição: Status ${response.status}`);
            grid.innerHTML = `<p class="error-message">Erro ao carregar cursos. Status: ${response.status}. Verifique o console do servidor e CORS.</p>`;
            return;
        }

        const cursos = await response.json();

        if (cursos.length === 0) {
            grid.innerHTML = `<p>Você ainda não criou nenhum curso. Clique em "+ Criar novo curso" para começar!</p>`;
            return;
        }

        grid.innerHTML = cursos.map(curso => `
            <article class="course-card-full">
                <div class="course-image" style="background-image: url('${curso.imageUrl || 'placeholder.jpg'}');"></div>
                <div class="course-content">
                    <div class="course-tags">
                        <span class="tag tag-level">${curso.level}</span>
                        <span class="tag tag-status">${curso.status || 'RASCUNHO'}</span>
                    </div>
                    <h3 class="course-title">${curso.title}</h3>
                    <p class="course-description">${curso.description}</p>
                    <div class="course-meta">
                        <div class="course-rating">
                            <span class="rating-stars">★★★★★</span>
                            <span class="rating-value">${curso.rating ? curso.rating.toFixed(1) : "0.0"}</span>
                        </div>
                    </div>
                    <button onclick="window.location.href='course-edit.html?id=${curso.id}'" class="btn btn-primary btn-block">
                        Editar Curso
                    </button>
                    <a href="course-detail.html?id=${curso.id}" style="display: block; text-align: center; margin-top: 10px;">Ver Página Pública</a>
                </div>
            </article>
        `).join("");

    } catch (err) {
        console.error("Erro fatal ao carregar cursos do professor:", err);
        grid.innerHTML = '<p class="error-message">Ocorreu um erro de rede. Verifique se o servidor está ativo.</p>';
    }
}