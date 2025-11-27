

const API_BASE = "http://localhost:8080";
const BASE_AVATAR_URL = API_BASE;
const postsContainer = document.getElementById("posts-container");
const formCreatePost = document.getElementById("form-create-post");

// Chamada inicial: exige login e carrega feed
document.addEventListener("DOMContentLoaded", () => {

    const type = getUserType();

    document.querySelectorAll("[data-show]").forEach(el => {
        const allowed = el.dataset.show.split(",");
        if (!allowed.includes(type) && !allowed.includes("ALL")) {
            el.style.display = "none";
        }
    });
   
    requireUser();
    loadFeed();
});


async function loadFeed() {
    postsContainer.innerHTML = "<p>Carregando posts...</p>";

    try {
        // Carrega todos os usuários
        const response = await fetch("http://localhost:8080/users/list");
        if (!response.ok) throw new Error("Erro ao carregar usuários");

        const users = await response.json();

        // Renderiza o feed com posts + autores
        renderPostsFromUsers(users);

    } catch (error) {
        console.error("Erro:", error);
        postsContainer.innerHTML = "<p>Erro ao carregar o feed.</p>";
    }
}

function getInitials(name) {
    if (!name) return '?';
    const parts = name.split(' ');
    if (parts.length >= 2) {
        return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
}

function renderPostsFromUsers(users) {
    postsContainer.innerHTML = "";

    // Percorre todos os usuários
    for (const user of users) {

        // Se o usuário não tem posts → ignora
        if (!user.posts || user.posts.length === 0) continue;

        // === NOVO: LÓGICA DO AVATAR ===
        let avatarHtml = '';
        const initials = getInitials(user.name);

        if (user.avatarUrl) {
            // Se houver URL, usa a imagem
            const fullUrl = `${BASE_AVATAR_URL}${user.avatarUrl}`;

            avatarHtml = `
                <div class="post-avatar" style="
                    background-image: url('${fullUrl}');
                    background-size: cover;
                    background-position: center;
                "></div>
            `;
        } else {
            // Se não houver URL, usa as iniciais
            avatarHtml = `
                <div class="post-avatar post-avatar-initials">
                    <span>${initials}</span>
                </div>
            `;
        }
        // ============================

        // Percorre todos os posts desse usuário
        for (const post of user.posts) {

            const item = document.createElement("div");
            item.classList.add("post-card");

            item.innerHTML = `
                <div class="post-header">
                    
                    ${avatarHtml} <div>
                        <strong>${user.name}</strong>

                        <div class="post-meta">
                            ${new Date(post.createDate).toLocaleString()}
                            • <span class="status-${post.status}">${post.status}</span>
                        </div>

                        <div class="post-author-extra">
                            <small>${user.accountType}</small><br>
                            <small>Área: ${user.specializedArea ?? "Não informada"}</small>
                        </div>
                    </div>
                </div>

                <h2 class="post-title">${post.title}</h2>

                <p class="post-content">${post.content}</p>
            `;

            postsContainer.appendChild(item);
        }
    }
}

const modal = document.getElementById("modal-overlay");
const titleInput = document.getElementById("modal-title-input");
const contentInput = document.getElementById("modal-content-input");

// Abrir modal (botão “Novo Post” no seu layout)
document.getElementById("btn-open-modal").onclick = () => {
    modal.style.display = "flex";
};
 
// Fechar modal
function closePostModal() {
    modal.style.display = "none";
}

async function publishPostModal() {
    const title = titleInput.value.trim();
    const content = contentInput.value.trim();
    const userId = getUserId();
    let userType = getUserType();

    if (userType === 'PROFESSOR') {
        userType  = 'TEACHER';
    } else if (userType === 'ESTUDANTE') {
        userType = 'STUDENT'; 
    }

    if (!title || !content) {
        alert("Preencha título e conteúdo!");
        return;
    }

    const response = await fetch("http://localhost:8080/posts", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            title,
            content,
            status: "ATIVA",
            author: {
                type: userType,
                id: userId 
            }
        })
    });

    if (!response.ok) {
        alert("Erro ao publicar: " + response.status);
        return;
    }

    closePostModal();
    titleInput.value = "";
    contentInput.value = "";

    loadFeed();
}

// Fecha modal clicando fora
modal.onclick = (e) => {
    if (e.target === modal) {
        closePostModal();
    }
};