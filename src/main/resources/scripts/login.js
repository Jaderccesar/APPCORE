document.addEventListener("DOMContentLoaded", function () {

    const form = document.querySelector("form");
    const messageBox = document.getElementById("message-box");
    const btnSubmit = document.getElementById("btn-submit");

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        // Limpa mensagens
        messageBox.classList.add("hidden");
        messageBox.textContent = "";

        try {
          const url = `http://localhost:8080/users/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;
          
          const response = await fetch(url, { method: "POST" });

          // pega sempre como texto
          const text = await response.text();

          let data = null;

          // tenta converter para json
          try {
              data = JSON.parse(text);
          } catch (err) {
              // não é JSON, então mantém text mesmo
          }

          // se deu erro na requisição (400, 404, 500)
          if (!response.ok) {
              // se vier JSON com mensagem
              if (data && data.message) {
                  throw new Error(data.message);
              }

              // se vier texto puro, usa o texto
              throw new Error(text || "Erro desconhecido");
          }

          // se a resposta foi OK mas não veio JSON → erro do backend
          if (!data) {
              throw new Error("Resposta inválida do servidor.");
          }

          // pega o id do usuário
          const userId = data?.id;
          const accountType = data?.accountType;

          console.log(data);

          if (!userId) {
              throw new Error("O servidor não retornou o ID do usuário.");
          }

          localStorage.setItem("userId", userId);
          localStorage.setItem("userType", accountType);

          if (accountType === 'PROFESSOR') {
             window.location.href = `course-teacher.html`;
          } else if (accountType === 'ESTUDANTE') {
              window.location.href = `course.html`; 
          }
          

      } catch (error) {
          messageBox.textContent = error.message;
          messageBox.classList.remove("hidden");
          messageBox.classList.add("bg-red-100", "text-red-700", "border", "border-red-300");
      }
    });
});
