<!DOCTYPE html>
<html>
<head>
    <script>
    window.MathJax = {
      loader: {load: ['input/mml', 'output/chtml']},
      startup: {
        ready: () => {
          MathJax.startup.defaultReady();
          // Báo cho Android biết là MathJax đã sẵn sàng
          if(window.Android) window.Android.onReady();
        }
      }
    };
    </script>
    <script id="MathJax-script" async src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/startup.js"></script>
    <script>
        // Hàm chuyển đổi chính
        function convertMmlToTex(mml) {
            try {
                // Sử dụng MathJax để convert (cách này hơi hack một chút vì MathJax 3 ưu tiên render)
                // Tuy nhiên, cách an toàn và nhẹ hơn là dùng thư viện 'mathml-to-latex' thuần JS.
                // Dưới đây là ví dụ giả lập nếu bạn inject thư viện mathml-to-latex
                // Để đơn giản cho demo này, ta sẽ dùng logic cơ bản:

                // Thực tế: MathJax V3 khó output ngược ra TeX string hơn V2.
                // KHUYÊN DÙNG: Hãy load thư viện 'mathml2latex' (nhẹ hều) thay vì MathJax.
                // Ví dụ: const latex = MathML2LaTeX.convert(mml);

                // Giả sử ta đã load thư viện mathml2latex:
                // window.Android.onConversionResult(convertedLatex);
            } catch (e) {
                window.Android.onError(e.toString());
            }
        }
    </script>
    <script src="https://unpkg.com/mathml-to-latex@1.3.0/dist/mathml-to-latex.min.js"></script>
    <script>
        function doConvert(mmlString) {
            try {
                const latex = MathMLToLaTeX.convert(mmlString);
                window.Android.onConversionResult(latex);
            } catch(e) {
                // Fallback nếu lỗi hoặc trả về nguyên gốc
                window.Android.onConversionResult(mmlString);
            }
        }
    </script>
</head>
<body></body>
</html>