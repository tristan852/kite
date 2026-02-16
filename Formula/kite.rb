class Kite < Formula
  desc "A Connect Four solver"
  homepage "https://github.com/tristan852/kite"
  version "1.16.2"

  if OS.mac?
    url "https://github.com/tristan852/kite/releases/download/v1.16.2/kite-1.16.2-macos-x64.tar.gz"
    sha256 "7C8CE59BF6AFC2BBFC78F74A6E4FE139BB4FF86B224C6C0E3899DFC5F176FDB7"
  elsif OS.linux?
    url "https://github.com/tristan852/kite/releases/download/v1.16.2/kite-1.16.2-linux-x64.tar.gz"
    sha256 "AD98E6CB81B19170E3AF39380B0F32F8B7B0E8EAD8F0B208D8769666EF5CA9DF"
  end

  def install
    bin.install "kite"
  end

  test do
    output = shell_output("#{bin}/kite --version")
    assert_match "1.16.2", output
  end
end
