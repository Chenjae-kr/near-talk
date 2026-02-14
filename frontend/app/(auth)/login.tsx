import { View, Text } from "react-native";

export default function LoginScreen() {
  return (
    <View className="flex-1 items-center justify-center px-6">
      <Text className="text-2xl font-bold">Near Talk</Text>
      <Text className="mt-4 text-gray-500">소셜 로그인으로 시작하세요</Text>
    </View>
  );
}
