document.addEventListener("DOMContentLoaded", () => {

    const teacherId = requireUser();
    const type = getUserType();

    document.querySelectorAll("[data-show]").forEach(el => {
            const allowed = el.dataset.show.split(","); 

            if (!allowed.includes(type) && !allowed.includes("ALL")) {
                el.style.display = "none";
            }
    });
  
    if (type !== "PROFESSOR") {
        alert("Acesso negado: esta página é somente para professores.");
        window.location.href = "home.html"; 
        return;
  }
  
  const videosContainer = document.getElementById("videos-container");
  const addVideoBtn = document.getElementById("add-video-btn");
  const form = document.getElementById("course-form");

  addVideoBtn.addEventListener("click", () => {
    const index = videosContainer.children.length + 1;

    const videoBlock = document.createElement("div");
    videoBlock.classList.add("form-group");
    videoBlock.innerHTML = `
      <div class="form-row" style="margin-bottom:1rem;">
        <div class="form-group">
          <label class="form-label">Título do Vídeo *</label>
          <input type="text" class="form-input video-title" placeholder="Ex: Introdução ao Curso" required />
        </div>
        <div class="form-group">
          <label class="form-label">Ordem *</label>
          <input type="number" class="form-input video-order" min="1" value="${index}" required />
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">Descrição *</label>
        <textarea class="form-textarea video-description" rows="2" placeholder="Descrição do vídeo..." required></textarea>
      </div>
      <div class="form-group">
        <label class="form-label">Arquivo de Vídeo *</label>
        <input type="file" accept="video/*" class="form-input video-file" required />
        <p class="form-help">Selecione o vídeo (.mp4, .mov, etc)</p>
      </div>
      <hr style="margin: 1.5rem 0; border: 0; border-top: 1px solid #dee2e6;">
    `;

    videosContainer.appendChild(videoBlock);
  });

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const courseData = {
      title: form.title.value,
      description: form.description.value,
      price: parseFloat(form.price.value),
      imageUrl: form.image_url.value,
      rating: 0,
      level: form.level.value,
      status: form.status.value,
      workload: parseInt(form.workload.value),
      certificateEnabled: form.certificate_enabled.checked,
      teacher: {
            id: parseInt(teacherId),
            type: "TEACHER" 
          }
    };

    try {
      const courseRes = await fetch("http://localhost:8080/courses/save", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(courseData)
      });

      if (!courseRes.ok) throw new Error("Erro ao salvar curso"); 

      const course = await courseRes.json();
      const courseId = course.id;

      const videoBlocks = videosContainer.querySelectorAll(".form-group");

      for (const block of videoBlocks) {
        const title = block.querySelector(".video-title")?.value;
        const description = block.querySelector(".video-description")?.value;
        const orderNumber = block.querySelector(".video-order")?.value;
        const fileInput = block.querySelector(".video-file");

        if (!title || !fileInput || fileInput.files.length === 0) continue;

        const formData = new FormData();
        formData.append("file", fileInput.files[0]);

        const uploadRes = await fetch("http://localhost:8080/s3/upload", {
          method: "POST",
          body: formData
        });

        if (!uploadRes.ok) throw new Error("Erro ao enviar vídeo para o S3");

        const { videoUrl } = await uploadRes.json();

        await fetch(`http://localhost:8080/courses/${courseId}/videos`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            title,
            description,
            videoUrl,
            orderNumber: parseInt(orderNumber)
          })
        });

        Swal.fire({
          icon: "success",
          title: "",
          text: "Curso cadastrado com sucesso!",
          timer: 2000,
          showConfirmButton: false,
          position: "top",
        }).then(() => {
             window.location.href = `course-teacher.html`;
        });

      }
    } catch (err) {

      Swal.fire({
        icon: "error",
        title: "",
        text: "Ops..., algo deu errado!",
        timer: 2500,
        showConfirmButton: false,
        position: "top",
      });
      
    }
  });
}); 
