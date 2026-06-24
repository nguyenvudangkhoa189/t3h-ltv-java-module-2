document.addEventListener("DOMContentLoaded", function () {
    const deleteBtn = document.getElementById("deleteBtn");
    if (!deleteBtn) {
        return;
    }

    deleteBtn.addEventListener("click", async function () {
        const productId = deleteBtn.dataset.productId;
        if (!confirm("Bạn có chắc muốn xóa sản phẩm này?")) {
            return;
        }

        try {
            const response = await fetch("/api/products/" + productId, {
                method: "DELETE"
            });

            if (!response.ok) {
                throw new Error("Request failed");
            }

            alert("Đã xóa sản phẩm ID: " + productId);
            window.location.href = "/home";
        } catch (error) {
            alert("Xóa sản phẩm thất bại. Vui lòng thử lại.");
        }
    });
});
