async function carregarCursos() {
      const container = document.querySelector(".courses-grid");
      container.innerHTML = "<p>Carregando cursos...</p>";

      try {
        const response = await fetch("http://localhost:8080/courses/list");
        const cursos = await response.json();

        if (cursos.length === 0) {
          container.innerHTML = "<p>Nenhum curso disponível no momento.</p>";
          return;
        }

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
                  <a href="#" class="btn btn-primary btn-block">Ver Curso</a>
              </div>
          </article>
        `).join("");
      } catch (err) {
        console.error("Erro:", err);
        container.innerHTML = "<p>Erro ao carregar os cursos.</p>";
      }
    }

    carregarCursos();