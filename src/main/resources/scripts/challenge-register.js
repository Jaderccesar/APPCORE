const API_BASE = "http://localhost:8080/challenges";
    let questionIndex = 0;
    const questionsContainer = document.getElementById('questionsContainer');

    function createQuestionBlock(idx, data) {
      const q = data || { text: '', alternatives: [{text:''},{text:''},{text:''},{text:''},{text:''}], correct: 'A' };
      const block = document.createElement('div');
      block.className = 'question-block';
      block.dataset.idx = idx;
      block.innerHTML = `
        <div class="question-header" style="display:flex;justify-content:space-between;align-items:center">
          <h3>Questão ${idx+1}</h3>
          <button type="button" class="btn btn-ghost btn-sm btn-remove" data-idx="${idx}">Remover</button>
        </div>
        <div class="form-group">
          <textarea class="form-textarea q-text" rows="3" placeholder="Enunciado" required>${escapeHtml(q.text)}</textarea>
        </div>
        <div class="alternatives">
          ${['A','B','C','D','E'].map((letter,i)=>`
            <label class="alternative-label" style="display:flex;gap:8px;align-items:center;margin-bottom:8px">
              <input type="radio" name="q_${idx}_correct" value="${letter}" ${q.correct === letter ? 'checked' : ''} required>
              <span style="width:20px;font-weight:700">${letter}</span>
              <input type="text" class="alternative-input" placeholder="Alternativa ${letter}" value="${escapeHtml(q.alternatives[i]?.text || '')}" required style="flex:1">
            </label>
          `).join('')}
        </div>
        <hr style="margin:12px 0">
      `;

      block.querySelector('.btn-remove').addEventListener('click', (e)=>{
        e.preventDefault();
        block.remove();
      });
      return block;
    }

    document.getElementById('addQuestionBtn').addEventListener('click', ()=>{
      const block = createQuestionBlock(questionIndex++);
      questionsContainer.appendChild(block);
      block.querySelector('.q-text').focus();
    });

    function collectQuestions() {
      const blocks = Array.from(questionsContainer.querySelectorAll('.question-block'));
      return blocks.map((b, idx)=>{
        const text = b.querySelector('.q-text').value.trim();
        const altInputs = Array.from(b.querySelectorAll('.alternative-input'));
        const correct = b.querySelector(`input[name="q_${b.dataset.idx}_correct"]:checked`);
        if(!correct) throw new Error('Marque a alternativa correta em todas as questões.');
        const alternatives = altInputs.map(ai => ({ text: ai.value.trim(), correct: false }));
        const correctLetter = correct.value;
        const letterIndex = ['A','B','C','D','E'].indexOf(correctLetter);
        if(letterIndex >= 0) alternatives[letterIndex].correct = true;
        return { enunciado: text, alternatives };
      });
    }

    function escapeHtml(s){ return (s+'').replace(/[&<>"']/g, c=>({ '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;' }[c])); }

    async function submitForm(e) {
      e.preventDefault();
      try {
        const id = document.getElementById('challengeId').value || null;
        const title = document.getElementById('title').value.trim();
        const description = document.getElementById('description').value.trim();
        const maxScore = parseInt(document.getElementById('maxScore').value,10) || 0;
        const startDateRaw = document.getElementById('startDate').value;
        const endDateRaw = document.getElementById('endDate').value;

        if(!title || !description) return alert('Preencha título e descrição.');
        if(!startDateRaw || !endDateRaw) return alert('Defina datas de início e término.');

        const questions = collectQuestions();
        if(questions.length === 0) return alert('Adicione pelo menos uma questão.');

        const startDateIso = new Date(startDateRaw).toISOString();
        const endDateIso = new Date(endDateRaw).toISOString();

        const payload = {
          title,
          description,
          maxScore,
          startDate: startDateIso,
          endDate: endDateIso,
          questions
        };

        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/update/${id}` : `${API_BASE}/save`;

        const res = await fetch(url, {
          method,
          headers: { 'Content-Type':'application/json' },
          body: JSON.stringify(payload)
        });

        if (!res.ok) {
          const txt = await res.text();
          throw new Error(txt || 'Erro no servidor');
        }

        alert('Desafio salvo com sucesso!');
        window.location.href = 'challenge.html';

      } catch (err) {
        console.error(err);
        alert('Erro: ' + (err.message || err));
      }
    }

    document.getElementById('challengeForm').addEventListener('submit', submitForm);

    async function loadIfEdit() {
      const params = new URLSearchParams(location.search);
      const id = params.get('id');
      if (!id) return;
      document.getElementById('formTitle').textContent = 'Editar Desafio';
      document.getElementById('challengeId').value = id;

      try {
        const res = await fetch(`${API_BASE}/${id}`);
        if (!res.ok) throw new Error('Não encontrado');
        const ch = await res.json();
        document.getElementById('title').value = ch.title || '';
        document.getElementById('description').value = ch.description || '';
        document.getElementById('maxScore').value = ch.maxScore ?? '';
        if (ch.startDate) document.getElementById('startDate').value = toInputDatetime(ch.startDate);
        if (ch.endDate) document.getElementById('endDate').value = toInputDatetime(ch.endDate);

        questionsContainer.innerHTML = '';
        questionIndex = 0;
        if (ch.questions && ch.questions.length) {
          ch.questions.forEach(q=>{
            const block = createQuestionBlock(questionIndex++, mapQuestionFromBackend(q));
            questionsContainer.appendChild(block);
          });
        }
      } catch (err) {
        console.error(err);
        alert('Falha ao carregar desafio para edição.');
      }
    }

    function mapQuestionFromBackend(q) {

      const mapped = { text: q.enunciado || '', alternatives: [], correct: 'A' };
      const alts = q.alternatives || [];
      for(let i=0;i<5;i++){
        mapped.alternatives.push({ text: alts[i] ? (alts[i].text || '') : '' });
        if (alts[i] && alts[i].correct) mapped.correct = ['A','B','C','D','E'][i];
      }
      return mapped;
    }

    function toInputDatetime(iso) {

      const d = new Date(iso);
      if (isNaN(d)) return '';

      const pad = n=>n.toString().padStart(2,'0');
      const yyyy = d.getFullYear();
      const MM = pad(d.getMonth()+1);
      const dd = pad(d.getDate());
      const hh = pad(d.getHours());
      const mm = pad(d.getMinutes());
      return `${yyyy}-${MM}-${dd}T${hh}:${mm}`;
    }

    document.addEventListener('DOMContentLoaded', ()=>{

      if (!location.search.includes('id=')) {
        document.getElementById('addQuestionBtn').click();
      }
      loadIfEdit();
    });