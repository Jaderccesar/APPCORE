

const API_BASE = "http://localhost:8080";  
const postsContainer = document.getElementById("posts-container");
const formCreatePost = document.getElementById("form-create-post");

// Chamada inicial: exige login e carrega feed
document.addEventListener("DOMContentLoaded", () => {
    requireUser();
    loadFeed();
});


async function loadFeed() {
    postsContainer.innerHTML = "<p>Carregando posts...</p>";

    try {
        const response = await fetch(`${API_BASE}/posts`);
        if (!response.ok) {
            throw new Error("Erro ao carregar posts");
        }

        const posts = await response.json();
        renderPosts(posts);

    } catch (error) {
        console.error("Erro:", error);
        postsContainer.innerHTML = "<p>Erro ao carregar o feed.</p>";
    }
}


function renderPosts(posts) {
    if (!posts || posts.length === 0) {
        postsContainer.innerHTML = "<p>Nenhum post encontrado.</p>";
        return;
    }

    postsContainer.innerHTML = "";

    posts.forEach(post => {
        const author = post.author || {};
        
        const item = document.createElement("div");
        item.classList.add("post-card");

        item.innerHTML = `
            <div class="post-header">
                <div class="post-author-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="26" height="26" fill="none"
                         stroke="currentColor" stroke-width="2" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round"
                              d="M15.75 7.5a3.75 3.75 0 11-7.5 0 3.75 3.75 0 017.5 0zM4.5 20.25a8.25 8.25 0 0115 0"/>
                    </svg>
                </div>

                <div>
                    <strong>${author.name ?? "Autor desconhecido"}</strong>

                    <div class="post-meta">
                        ${new Date(post.createDate).toLocaleString()}
                        • <span class="status-${post.status}">${post.status}</span>
                    </div>

                    <div class="post-author-extra">
                        <small>Tipo: ${author.accountType ?? "-"}</small><br>
                        ${author.specializedArea ? `<small>Área: ${author.specializedArea}</small>` : ""}
                    </div>

                </div>
            </div>

            <h2 class="post-title">${post.title}</h2>

            <p class="post-content">${post.content}</p>
        `;

        postsContainer.appendChild(item);
    });
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

// Publicar post
async function publishPostModal() {
    const title = titleInput.value.trim();
    const content = contentInput.value.trim();
    const userId = getUserId();
    const userType = getUserType();

    if (!title || !content) {
        alert("Preencha título e conteúdo!");
        return;
    }

    const response = await fetch(`http://localhost:8080/posts`, {
         method: "POST",
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

  console.log("Response:", response);

    // Fecha modal e limpa inputs
    closePostModal();
    titleInput.value = "";
    contentInput.value = "";

    // Recarrega a lista de posts
    loadFeed() 
}

// Fecha modal clicando fora
modal.onclick = (e) => {
    if (e.target === modal) {
        closePostModal();
    }
};