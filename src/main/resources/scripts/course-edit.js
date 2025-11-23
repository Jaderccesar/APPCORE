// 1. Variáveis e Validação Inicial
const params = new URLSearchParams(window.location.search);
const courseId = params.get("id");

// Garante que o usuário está logado e é professor ANTES de prosseguir
const teacherId = requireUser(); // Assume que requireUser() verifica e retorna o ID
const type = getUserType();

// O código abaixo DEVE estar na página de edição (course-edit.html)
document.querySelectorAll("[data-show]").forEach(el => {
    const allowed = el.dataset.show.split(",");
    if (!allowed.includes(type) && !allowed.includes("ALL")) {
        el.style.display = "none";
    }
});

if (type !== "PROFESSOR") {
    alert("Acesso negado: esta página é somente para professores.");
    window.location.href = "home.html";
}

if (!courseId) {
    // Se não há ID, pode ser uma tentativa de acesso direto. Redireciona para a lista.
    Swal.fire("Erro", "Nenhum ID de curso informado. Redirecionando...", "error").then(() => {
        window.location.href = "course-teacher.html";
    });
}


// 2. Carregamento dos Dados
async function carregarCurso() {
    try {
        const res = await fetch(`http://localhost:8080/courses/${courseId}`);

        if (!res.ok) {
            Swal.fire("Erro", "Curso não encontrado ou erro de API.", "error");
            return;
        }

        const curso = await res.json();
        console.log("Curso carregado:", curso);

        // Preenche os campos do formulário
        document.getElementById("title").value = curso.title || '';
        document.getElementById("description").value = curso.description || '';
        document.getElementById("price").value = curso.price || 0;
        document.getElementById("image_url").value = curso.imageUrl || '';
        document.getElementById("level").value = curso.level || '';
        document.getElementById("status").value = curso.status || 'DRAFT';
        document.getElementById("workload").value = curso.workload || 0;
        document.getElementById("certificate_enabled").checked = curso.certificateEnabled || false;

        // Se o curso.teacher.id não for o mesmo do loggedUserId, o professor não deve editar.
        const courseTeacherId = curso.teacher && curso.teacher.id ? String(curso.teacher.id) : null;
        if (String(teacherId) !== courseTeacherId) {
            Swal.fire("Acesso negado", "Você não é o criador deste curso.", "error").then(() => {
                window.location.href = "course-teacher.html";
            });
            return;
        }

        carregarVideos(curso.videos || []);

    } catch (error) {
        console.error("Erro ao carregar curso para edição:", error);
        Swal.fire("Erro", "Não foi possível carregar o curso. Verifique o console.", "error");
    }
}

// Inicializa o carregamento se o ID existir
if (courseId) {
    carregarCurso();
}


// 3. Funções de Templates e Eventos (Vídeos)
function videoTemplate(id, index, title, description, url) {
    // Seu template de vídeo
    return `
    <div class="video-item" 
        data-id="${id ?? ''}" 
        data-url="${url ?? ''}"
        style="background:#fafafa; padding:20px; border-radius:12px; border:1px solid #e5e5e5; margin-bottom:22px;">
        <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:1rem;">
            <h3 style="margin:0;">Vídeo ${index}</h3>
            ${id ? `
                <button type="button" class="btn btn-danger btn-delete-video"
                    style="padding:6px 14px; background:#dc3545; color:white;">
                    Excluir
                </button>
            ` : `
                <button type="button" class="btn btn-danger btn-remove-temp"
                    style="padding:6px 14px; background:#dc3545; color:white;">
                    Remover
                </button>
            `}
        </div>
        <div class="form-row" style="margin-bottom:1rem; display:flex; gap:20px;">
            <div class="form-group" style="flex:1;">
                <label class="form-label">Título *</label>
                <input type="text" class="form-input video-title" value="${title || ''}" required />
            </div>
            <div class="form-group" style="width:150px;">
                <label class="form-label">Ordem *</label>
                <input type="number" class="form-input video-order" min="1" value="${index}" required />
            </div>
        </div>
        <div class="form-group" style="margin-bottom:1rem;">
            <label class="form-label">Descrição *</label>
            <textarea class="form-textarea video-description">${description || ''}</textarea>
        </div>
        <div class="form-group">
            <label class="form-label">Arquivo *</label>
            <input type="file" accept="video/*" class="form-input video-file" />
        </div>
        ${url ? `
            <div style="margin-top:10px;">
                <a href="${url}" target="_blank" class="btn btn-primary">
                    Visualizar Atual
                </a>
            </div>
        ` : ""}
    </div>
    `;
}

function carregarVideos(videos) {
    const container = document.getElementById("videos-container");
    container.innerHTML = "";

    videos.forEach(v => {
        container.innerHTML += videoTemplate(v.id, v.orderNumber, v.title, v.description, v.videoUrl);
    });
}

// Listener para Adicionar Vídeo
document.getElementById("add-video-btn").addEventListener("click", () => {
    const container = document.getElementById("videos-container");
    const count = document.querySelectorAll(".video-item").length + 1;
    container.innerHTML += videoTemplate(null, count, "", "", "");
});

// Listener para Excluir/Remover Vídeo
document.addEventListener("click", async (e) => {
    if (e.target.classList.contains("btn-delete-video")) {
        const videoEl = e.target.closest(".video-item");
        const videoId = videoEl.dataset.id;

        const confirm = await Swal.fire({
            title: "Excluir vídeo?",
            text: "Esta ação é permanente no banco de dados!",
            icon: "warning",
            showCancelButton: true
        });

        if (!confirm.isConfirmed) return;

        // Requisição para o Backend
        await fetch(`http://localhost:8080/courses/${courseId}/videos/${videoId}`, {
            method: "DELETE"
        });

        videoEl.remove();
        Swal.fire("Sucesso", "Vídeo excluído.", "success");

    } else if (e.target.classList.contains("btn-remove-temp")) {
        e.target.closest(".video-item").remove();
    }
});


// 4. Lógica de Submissão do Formulário
document.getElementById("course-edit-form").addEventListener("submit", async (e) => {
    e.preventDefault();

    // Reúne os dados do curso
    const cursoData = {
        title: document.getElementById("title").value,
        description: document.getElementById("description").value,
        price: Number(document.getElementById("price").value),
        level: document.getElementById("level").value,
        status: document.getElementById("status").value,
        workload: Number(document.getElementById("workload").value),
        imageUrl: document.getElementById("image_url").value,
        certificateEnabled: document.getElementById("certificate_enabled").checked
    };

    // Atualiza os dados principais do curso
    await fetch(`http://localhost:8080/courses/update/${courseId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cursoData)
    });

    // Atualiza ou cria os vídeos
    const videos = [...document.querySelectorAll(".video-item")];

    for (const v of videos) {
        const id = v.dataset.id;
        const title = v.querySelector(".video-title").value;
        const description = v.querySelector(".video-description").value;
        const order = v.querySelector(".video-order").value;
        const file = v.querySelector(".video-file").files[0];
        const existingUrl = v.dataset.url;

        let videoUrl = existingUrl;

        // Se um novo arquivo foi selecionado, faz o upload
        if (file) {
            const fd = new FormData();
            fd.append("file", file);

            const uploadRes = await fetch("http://localhost:8080/s3/upload", {
                method: "POST",
                body: fd
            });

            const up = await uploadRes.json();
            videoUrl = up.videoUrl;
        }

        const payload = {
            title,
            description,
            videoUrl,
            orderNumber: Number(order)
        };

        // Requisição PUT ou POST para Vídeo
        if (id) {
            // Atualizar Vídeo
            await fetch(`http://localhost:8080/courses/${courseId}/videos/${id}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
        } else {
            // Criar Novo Vídeo
            await fetch(`http://localhost:8080/courses/${courseId}/videos`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });
        }
    }

    // Sucesso
    Swal.fire({
        icon: "success",
        title: "Sucesso!",
        text: "Curso e vídeos atualizados com sucesso!",
        timer: 2000,
        showConfirmButton: false,
        position: "top",
    }).then(() => {
        // Volta para a lista de cursos do professor
        window.location.href = `course-teacher.html`;
    });
});