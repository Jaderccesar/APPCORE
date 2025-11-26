document.addEventListener("DOMContentLoaded", function () {

    const form = document.querySelector("form");
    const messageBox = document.getElementById("message-box");
    const btnSubmit = document.getElementById("btn-submit");

    form.addEventListener("submit", async function (e) {
        e.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        messageBox.classList.add("hidden");
        messageBox.textContent = "";

        try {
          const url = `http://localhost:8080/users/login?email=${encodeURIComponent(email)}&password=${encodeURIComponent(password)}`;
          
          const response = await fetch(url, { method: "POST" });

          const text = await response.text();

          let data = null;

          try {
              data = JSON.parse(text);
          } catch (err) {
          }

          if (!response.ok) {
              if (data && data.message) {
                  throw new Error(data.message);
              }

              throw new Error(text || "Erro desconhecido");
          }

          if (!data) {
              throw new Error("Resposta inválida do servidor.");
          }

          const userId = data?.id;
          const accountType = data?.accountType;

          console.log(data);

          if (!userId) {
              throw new Error("O servidor não retornou o ID do usuário.");
          }

          localStorage.setItem("user", JSON.stringify(data));
          localStorage.setItem("userId", data.id);
          localStorage.setItem("userType", data.accountType);

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
