    const API_BASE = "http://localhost:8080/challenges";

    function isoDateShort(dateString) {
      if (!dateString) return "";
      try {
        return new Date(dateString).toLocaleDateString('pt-BR');
      } catch { return dateString; }
    }

    async function loadChallenges() {
      const grid = document.getElementById("challengeGrid");
      const loading = document.getElementById("loadingText");
      try {
        const res = await fetch(`${API_BASE}/list`);
        if (!res.ok) throw new Error("Erro ao carregar");
        const challenges = await res.json();
        grid.innerHTML = "";

        if (!challenges || challenges.length === 0) {
          grid.innerHTML = "<p>Nenhum desafio cadastrado.</p>";
          return;
        }

        challenges.forEach(ch => {
          const ended = ch.endDate ? new Date(ch.endDate) < new Date() : false;
          const daysLeft = ch.endDate ? Math.ceil((new Date(ch.endDate) - new Date()) / (1000*60*60*24)) : null;
          const questionCount = ch.questions ? ch.questions.length : (ch.questionCount ?? 0);

          const card = document.createElement("article");
          card.className = "course-card-full challenge-card";
          card.innerHTML = `
            <div class="course-card-image">
              <div class="placeholder-image" style="background:linear-gradient(135deg,#6772ff 0%,#7b61ff 100%);">
                <span style="font-size:42px;color:white">🧩</span>
              </div>
              <span class="badge ${ended ? 'badge-warning' : 'badge-success'}">${ended ? 'Encerrado' : 'Aberto'}</span>
            </div>
            <div class="course-card-content">
              <h3 class="course-card-title">${escapeHtml(ch.title)}</h3>
              <p class="course-card-description">${escapeHtml(ch.description || '')}</p>
              <div class="course-card-meta">
                <span class="meta-item">🏆 ${ch.maxScore ?? '—'} pts</span>
                <span class="meta-item">📋 ${questionCount} questões</span>
                <span class="meta-item">Início: ${isoDateShort(ch.startDate)}</span>
                <span class="meta-item">Fim: ${isoDateShort(ch.endDate)}</span>
                ${!ended && daysLeft ? `<span class="meta-item">🔥 ${daysLeft} dias</span>` : ''}
              </div>
              <div style="margin-top:12px;display:flex;gap:8px;">
                <a href="#" data-id="${ch.id}" class="btn btn-primary btn-block btn-view">Ver</a>
                <a href="challenge-register.html?id=${ch.id}" class="btn btn-outline">Editar</a>
              </div>
            </div>
          `;
          grid.appendChild(card);
        });

        document.getElementById('searchInput').addEventListener('input', (e)=>{
          const term = e.target.value.toLowerCase();
          document.querySelectorAll('.challenge-card').forEach(card=>{
            const title = card.querySelector('.course-card-title').textContent.toLowerCase();
            const desc = card.querySelector('.course-card-description').textContent.toLowerCase();
            card.style.display = (title.includes(term) || desc.includes(term)) ? '' : 'none';
          });
        });


        document.querySelectorAll('.btn-view').forEach(b=>{
          b.addEventListener('click', (ev)=>{
            ev.preventDefault();
            const id = b.getAttribute('data-id');
            window.location.href = `challenge-view.html?id=${id}`;
          });
        });

      } catch (err) {
        console.error(err);
        loading.textContent = "Erro ao carregar desafios.";
      }
    }

    function escapeHtml(s){ return (s+'').replace(/[&<>"']/g, c=>({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;' }[c])); }

    document.addEventListener('DOMContentLoaded', loadChallenges);