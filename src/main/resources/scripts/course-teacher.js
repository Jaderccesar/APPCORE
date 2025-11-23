document.addEventListener("DOMContentLoaded", () => {
    const teacherId = requireUser();
    const type = getUserType();

  
    if (type !== "PROFESSOR") {
        alert("Acesso negado: esta página é somente para professores.");
        window.location.href = "home.html"; 
        return;
    }

    carregarCursosProfessor(teacherId);
});

async function carregarCursosProfessor(teacherId) {
    const container = document.getElementById("teacher-courses-grid");
    container.innerHTML = "<p>Carregando cursos...</p>";

    if (!teacherId) {
        container.innerHTML = "<p>Erro: ID do professor não informado na URL.</p>";
        return;
    }

    try {
        // 2. Chamar sua API correta
        const response = await fetch(`http://localhost:8080/courses/teacher/${teacherId}`);

        if (!response.ok) {
            throw new Error("Erro ao carregar cursos do professor");
        }

        let cursos = await response.json();

        if (cursos.length === 0) {
            container.innerHTML = "<p>Você ainda não criou nenhum curso.</p>";
            return;
        }

        // 3. Renderizar cada curso com o layout original
        container.innerHTML = cursos.map(curso => `
            <article class="course-card-full">
                <div class="course-image" style="background-image: url('${curso.imageUrl}');"></div>

                <div class="course-content">
                    <div class="course-tags">
                        <span class="tag tag-level">${curso.level}</span>
                        <span class="tag tag-price">${curso.price > 0 ? "R$ " + curso.price.toFixed(2) : "Gratuito"}</span>
                    </div>

                    <h3 class="course-title">${curso.title}</h3>
                    <p class="course-description">${curso.description}</p>

                    <div class="course-meta">
                        <div class="course-rating">
                            <span class="rating-stars">★★★★★</span>
                            <span class="rating-value">${curso.rating || "0.0"}</span>
                        </div>
                        <div class="course-workload">
                            ⏱ ${curso.workload}h
                        </div>
                    </div>

                    <a href="#" class="btn btn-primary btn-block">Gerenciar Curso</a>
                </div>
            </article>
        `).join("");

    } catch (err) {
        console.error("Erro:", err);
        container.innerHTML = "<p>Erro ao carregar seus cursos.</p>";
    }
}