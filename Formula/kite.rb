class Kite < Formula
  desc "A Connect Four solver"
  homepage "https://github.com/tristan852/kite"
  version "1.16.3"

  if OS.mac?
    url "https://github.com/tristan852/kite/releases/download/v1.16.3/kite-1.16.3-macos-x64.tar.gz"
    sha256 "8E3FE5B5D2DFBC9AA6F4C70C66F1B016A8068DB01194615E034AB372B4C2BD9E"
  elsif OS.linux?
    url "https://github.com/tristan852/kite/releases/download/v1.16.3/kite-1.16.3-linux-x64.tar.gz"
    sha256 "3FC22CC6190DFA38B10B3B8BB50C4C6873399A5AD92B68DBD32613D01E11DB46"
  end

  def install
    bin.install "kite"
  end

  test do
    output = shell_output("#{bin}/kite --version")
    assert_match "1.16.3", output
  end
end
