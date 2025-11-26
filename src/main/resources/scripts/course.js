let currentListType = 'ALL_AVAILABLE';

document.addEventListener('DOMContentLoaded', () => {
    setupVisibility();

    setupCourseMenuEvents();

    carregarCursos(currentListType);
});


function setupVisibility() {
    const type = getUserType();
    const userId = getUserId();

    document.querySelectorAll(".nav a[data-show]").forEach(el => {
        const allowed = el.dataset.show.split(",");
        el.style.display = (allowed.includes(type) || allowed.includes("ALL")) ? 'flex' : 'none';
    });

    const menuContainer = document.getElementById('student-course-menu');
    if (menuContainer) {
        if (type === 'ESTUDANTE' && userId) {
            menuContainer.style.display = 'flex';
        } else {
            menuContainer.style.display = 'none';
        }
    }
}

function setupCourseMenuEvents() {
    const menuContainer = document.getElementById('student-course-menu');

    if (menuContainer) {
        menuContainer.addEventListener('click', (e) => {
            const button = e.target.closest('.menu-btn');

            if (button) {
                const listType = button.getAttribute('data-filter');

                currentListType = listType;

                menuContainer.querySelectorAll('.menu-btn').forEach(btn => {
                    btn.classList.remove('active', 'btn-primary');
                    btn.classList.add('btn-outline');
                });

                button.classList.add('active', 'btn-primary');
                button.classList.remove('btn-outline');

                carregarCursos(currentListType);
            }
        });
    }
}

async function carregarCursos(listType) {

    const type = getUserType();
    const userId = getUserId();
    const container = document.querySelector(".courses-grid");

    const titleElement = document.getElementById('course-list-title');
    const subtitleElement = document.getElementById('course-list-subtitle');

    if (listType === 'PURCHASED') {
        titleElement.textContent = "Meus Cursos";
        subtitleElement.textContent = "Continue de onde parou em seus cursos adquiridos.";
    } else {
        titleElement.textContent = "Todos os Cursos Disponíveis";
        subtitleElement.textContent = "Explore nossa biblioteca completa de cursos e comece a aprender hoje.";
    }

    container.innerHTML = "<p>Carregando cursos...</p>";

    const shouldFetchWithUserId = userId && type === 'ESTUDANTE';

    try {
        const fetchUrl = shouldFetchWithUserId
            ? `http://localhost:8080/courses/list?userId=${userId}`
            : "http://localhost:8080/courses/list";

        const response = await fetch(fetchUrl);
        const cursos = await response.json();

        if (cursos.length === 0) {
            container.innerHTML = "<p>Nenhum curso disponível no momento.</p>";
            return;
        }

        let cursosFiltrados = cursos;

        if (shouldFetchWithUserId) {
            if (listType === 'ALL_AVAILABLE') {
                cursosFiltrados = cursos.filter(curso => !curso.isPurchased);

                if (cursosFiltrados.length === 0) {
                    container.innerHTML = "<p>Você já adquiriu todos os cursos disponíveis!</p>";
                    return;
                }

            } else if (listType === 'PURCHASED') {
                cursosFiltrados = cursos.filter(curso => curso.isPurchased);

                if (cursosFiltrados.length === 0) {
                    container.innerHTML = "<p>Você ainda não comprou nenhum curso. Explore os cursos disponíveis!</p>";
                    return;
                }
            }
        }

        container.innerHTML = cursosFiltrados.map(curso => {
            const isPurchased = shouldFetchWithUserId && curso.isPurchased;

            const buttonText = isPurchased ? 'Continuar Curso' : 'Ver Curso';
            const priceTag = isPurchased ? 'ADQUIRIDO' : curso.price > 0 ? "R$ " + curso.price.toFixed(2).replace('.', ',') : "Gratuito";

            return `
              <article class="course-card-full">
                  <div class="course-image" style="background-image: url('${curso.imageUrl}');"></div>
                  <div class="course-content">
                      <div class="course-tags">
                          <span class="tag tag-level">${curso.level}</span>
                          <span class="tag tag-price">${priceTag}</span>
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
                      <button onclick="verDetalhesCurso(${curso.id})" class="btn btn-primary btn-block">${buttonText}</button>
                  </div>
              </article>
            `;
        }).join("");
    } catch (err) {
        console.error("Erro ao carregar cursos:", err);
        container.innerHTML = "<p>Erro ao carregar os cursos. Verifique se o backend está rodando.</p>";
    }
}

function verDetalhesCurso(courseId) {
    window.location.href = `course-detail.html?id=${courseId}`;
}