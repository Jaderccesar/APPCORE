// profile-progress.js
// Coloque este script depois do profile.js (ou importe em profile.html)
// Depende de requireUser() / getUserId() que você já tem

document.addEventListener("DOMContentLoaded", () => {
  try {
    const userId = (typeof requireUser === 'function') ? requireUser() : (localStorage.getItem('userId') || null);
    if (!userId) {
      console.warn('Usuário não identificado - progress não carregado');
      return;
    }

    carregarProgressDoAluno(Number(userId));
  } catch (err) {
    console.error('Erro inicializando progress script', err);
  }
});

async function carregarProgressDoAluno(studentId) {
  const url = `http://localhost:8080/progress/student/${studentId}`;

  // elementos do DOM
  const coursesCountEl = document.getElementById('coursesCount');
  const pointsCountEl = document.getElementById('pointsCount');
  const badgesCountEl = document.getElementById('badgesCount');

  const courseProgressListEl = document.querySelector('.course-progress-list');
  const statsGrid = document.querySelector('.stats-grid');

  try {
    const res = await fetch(url);
    if (!res.ok) {
      console.warn('Não foi possível buscar progresso do aluno', res.status);
      return;
    }

    const data = await res.json();

    // Estrutura esperada:
    // data = { coursesInProgress: [...], stats: { coursesEnrolled, coursesCompleted, challengesSolved, points } }

    const stats = data.stats || {};
    const cursos = data.coursesInProgress || [];

    // Atualiza contadores no header
    if (coursesCountEl) coursesCountEl.textContent = stats.coursesEnrolled != null ? stats.coursesEnrolled : '-';
    if (pointsCountEl) pointsCountEl.textContent = stats.points != null ? stats.points.toLocaleString('pt-BR') : '0';
    // Badges: por enquanto usamos challengesSolved como proxy (ou adapte para certificates)
    if (badgesCountEl) badgesCountEl.textContent = stats.challengesSolved != null ? stats.challengesSolved : '0';

    // Atualiza os cards de estatísticas à direita (se existirem)
    // A página já contém 4 cards; vamos preencher seus .stat-number na ordem
    if (statsGrid) {
      const statNumbers = statsGrid.querySelectorAll('.stat-number');
      // ordem: Cursos Inscritos, Cursos Concluídos, Desafios Resolvidos, Eventos Participados (opcional)
      if (statNumbers.length >= 3) {
        statNumbers[0].textContent = stats.coursesEnrolled != null ? stats.coursesEnrolled : '0';
        statNumbers[1].textContent = stats.coursesCompleted != null ? stats.coursesCompleted : '0';
        statNumbers[2].textContent = stats.challengesSolved != null ? stats.challengesSolved : '0';
        // se houver 4º card, mantemos o valor existente ou zero
        if (statNumbers[3]) statNumbers[3].textContent = statNumbers[3].textContent || '0';
      }
    }

    // Preenche a lista de cursos em andamento
    if (courseProgressListEl) {
      courseProgressListEl.innerHTML = '';

      if (cursos.length === 0) {
        courseProgressListEl.innerHTML = `<p>Você não está matriculado em nenhum curso no momento.</p>`;
      } else {
        cursos.forEach(c => {
          const pct = c.percentage != null ? c.percentage : 0;
          const completedLessons = c.completedLessons != null ? c.completedLessons : 0;
          const totalLessons = c.totalLessons != null ? c.totalLessons : 0;

          const item = document.createElement('div');
          item.className = 'course-progress-item';
          item.innerHTML = `
            <div class="course-progress-info">
              <h4 class="course-title-link" data-course-id="${c.courseId}" style="cursor:pointer; text-decoration: underline;">${escapeHtml(c.title)}</h4>
              <p>${completedLessons} de ${totalLessons} aulas concluídas</p>
            </div>
            <div class="progress-bar-container">
              <div class="progress-bar">
                <div class="progress-fill" style="width: ${pct}%"></div>
              </div>
              <span class="progress-percentage">${pct}%</span>
            </div>
          `;
          courseProgressListEl.appendChild(item);
        });

        // adiciona event listener para abrir a página do curso ao clicar no título
        courseProgressListEl.querySelectorAll('.course-title-link').forEach(el => {
          el.addEventListener('click', (ev) => {
            const courseId = ev.currentTarget.getAttribute('data-course-id');
            if (courseId) window.location.href = `course-detail.html?id=${courseId}`;
          });
        });
      }
    }

  } catch (error) {
    console.error('Erro ao buscar progresso:', error);
  }
}

/* small helper */
function escapeHtml(s) {
  return String(s || '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
}
