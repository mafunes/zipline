import shared
import SwiftUI

@main
struct LocalizeApp: App {
    var contentView = ContentView()

    var body: some Scene {
        WindowGroup {
            contentView
        }
    }
}

struct ContentView: View {
    @State private var label = "..."
    @State private var key: String = ""
    @State private var binding: String = ""
     
    var body: some View {
        let scope = ExposedKt.mainScope()
        let localizeIos: PresentersLocalizeIos = PresentersLocalizeIos(scope: scope)
        
        TextField(
            "key",
            text: $binding
        )
        .font(.system(size: 36))
        .multilineTextAlignment(.leading)
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(12)
        .onChange(of: binding) {
            let st: String = localizeIos.getKey(text: $0.lowercased())
            self.label = st
        }

        
        Text(label)
            .font(.system(size: 36))
            .multilineTextAlignment(.leading)
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(12)

        .onAppear {
            localizeIos.start()
        }
    }
}
